package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class FlyingDevice extends FlyingEntity implements IFlyingAnimal {

    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(FlyingDevice.class, DataSerializers.VARINT);
    private final List<DeviceCommand> commands;
    private UUID owner;

    public FlyingDevice(EntityType<? extends FlyingDevice> type, World worldIn) {
        super(type, worldIn);
        moveController = new FlyingMovementController(this, 90, true);
        setPathPriority(PathNodeType.WATER, 0);
        commands = new ArrayList<>();
        owner = null;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(COLOR, DyeColor.WHITE.getId());
    }

    @Override
    public void livingTick() {
        for (int i = 0; i < commands.size(); i++) {
            DeviceCommand command = commands.get(i);
            command.passiveTick();
            if (command.shouldRemove()) {
                goalSelector.removeGoal(command);
                commands.remove(i);
                i--;
            }
        }
        super.livingTick();
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return super.isOnSameTeam(entityIn) || entityIn.getUniqueID().equals(owner) || (entityIn instanceof FlyingDevice && owner != null && owner.equals(((FlyingDevice) entityIn).owner));
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        if (item instanceof DyeItem && !stack.isEmpty() && player.getUniqueID().equals(owner)) {
            DyeColor color = ((DyeItem) item).getDyeColor();
            if (color != getColor()) {
                if (!player.isCreative()) {
                    stack.setCount(stack.getCount() - 1);
                }
                setColor(color);
                return ActionResultType.CONSUME;
            }
        }
        return super.applyPlayerInteraction(player, vec, hand);
    }

    public void addCommand(DeviceCommand command) {
        commands.forEach(goalSelector::removeGoal);
        commands.add(0, command);
        for (int i = 0; i < commands.size(); i++) {
            goalSelector.addGoal(i, commands.get(i));
        }
    }

    public List<DeviceCommand> getCommands() {
        return commands;
    }

    public DyeColor getColor() {
        return DyeColor.byId(dataManager.get(COLOR));
    }

    public void setColor(DyeColor color) {
        dataManager.set(COLOR, color.getId());
    }

    @Override
    public PathNavigator createNavigator(World worldIn) {
        return new FlyingPathNavigator(this, worldIn);
    }

    @Nullable
    public Entity getOwner() {
        if (owner != null && world instanceof ServerWorld) {
            return ((ServerWorld) world).getEntityByUuid(owner);
        }
        return null;
    }

    @Nullable
    public UUID getOwnerID() {
        return owner;
    }

    public void setOwnerID(UUID id) {
        owner = id;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void travel(Vector3d vec) {
        float speed = (float) getAttributeValue(Attributes.FLYING_SPEED);
        Entity owner = getOwner();
        if (owner != null) {
            speed *= ShinsuStats.get(owner).getTension((ServerWorld) world);
        }
        moveRelative(speed, vec);
        move(MoverType.SELF, getMotion());
        Vector3d motion = getMotion().scale(0.91);
        setMotion(motion);
        getLookController().setLookPosition(getEyePosition(1).add(motion));
    }

    @Override
    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently) {
        ItemStack item = getDeviceItem();
        item.getOrCreateChildTag(TowerOfGod.MOD_ID).putInt("Color", getColor().getId());
        entityDropItem(item);
    }

    protected ItemStack getDeviceItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Commands", Constants.NBT.TAG_LIST)) {
            for (INBT command : nbt.getList("Commands", Constants.NBT.TAG_COMPOUND)) {
                CommandType type = CommandType.valueOf(((CompoundNBT) command).getString("Type"));
                DeviceCommand c = type.createEmpty(this);
                c.deserializeNBT((CompoundNBT) command);
                commands.add(c);
                goalSelector.addGoal(0, c);
            }
        }
        if (nbt.contains("Owner", Constants.NBT.TAG_INT_ARRAY)) {
            owner = nbt.getUniqueId("Owner");
        }
        if (nbt.contains("Color", Constants.NBT.TAG_INT)) {
            dataManager.set(COLOR, nbt.getInt("Color"));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        ListNBT commands = new ListNBT();
        for (DeviceCommand command : this.commands) {
            commands.add(command.serializeNBT());
        }
        nbt.put("Commands", commands);
        if (owner != null) {
            nbt.putUniqueId("Owner", owner);
        }
        nbt.putInt("Color", dataManager.get(COLOR));
    }
}

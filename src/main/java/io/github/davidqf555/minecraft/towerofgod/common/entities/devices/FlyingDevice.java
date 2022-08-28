package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class FlyingDevice extends FlyingMob implements FlyingAnimal {

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(FlyingDevice.class, EntityDataSerializers.INT);
    private final List<DeviceCommand> commands;
    private UUID owner;

    public FlyingDevice(EntityType<? extends FlyingDevice> type, Level worldIn) {
        super(type, worldIn);
        moveControl = new FlyingMoveControl(this, 90, true);
        setPathfindingMalus(BlockPathTypes.WATER, 0);
        commands = new ArrayList<>();
        owner = null;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLOR, DyeColor.WHITE.getId());
    }

    @Override
    public void aiStep() {
        for (int i = 0; i < commands.size(); i++) {
            DeviceCommand command = commands.get(i);
            command.passiveTick();
            if (command.shouldRemove()) {
                goalSelector.removeGoal(command);
                commands.remove(i);
                i--;
            }
        }
        super.aiStep();
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        return super.isAlliedTo(entityIn) || entityIn.getUUID().equals(owner) || (entityIn instanceof FlyingDevice && owner != null && owner.equals(((FlyingDevice) entityIn).owner));
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (item instanceof DyeItem && !stack.isEmpty() && player.getUUID().equals(owner)) {
            DyeColor color = ((DyeItem) item).getDyeColor();
            if (color != getColor()) {
                if (!player.isCreative()) {
                    stack.setCount(stack.getCount() - 1);
                }
                setColor(color);
                return InteractionResult.CONSUME;
            }
        }
        return super.interactAt(player, vec, hand);
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
        return DyeColor.byId(entityData.get(COLOR));
    }

    public void setColor(DyeColor color) {
        entityData.set(COLOR, color.getId());
    }

    @Override
    public PathNavigation createNavigation(Level worldIn) {
        return new FlyingPathNavigation(this, worldIn);
    }

    @Nullable
    public Entity getOwner() {
        if (owner != null && level instanceof ServerLevel) {
            return ((ServerLevel) level).getEntity(owner);
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void travel(Vec3 vec) {
        float speed = (float) getAttributeValue(Attributes.FLYING_SPEED);
        Entity owner = getOwner();
        if (owner != null) {
            speed *= ShinsuStats.get(owner).getRawTension();
        }
        moveRelative(speed, vec);
        move(MoverType.SELF, getDeltaMovement());
        Vec3 motion = getDeltaMovement().scale(0.91);
        setDeltaMovement(motion);
        getLookControl().setLookAt(getEyePosition(1).add(motion));
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSourceIn, boolean attackedRecently) {
        ItemStack item = getDeviceItem();
        item.getOrCreateTagElement(TowerOfGod.MOD_ID).putInt("Color", getColor().getId());
        spawnAtLocation(item);
    }

    protected ItemStack getDeviceItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Commands", Tag.TAG_LIST)) {
            for (Tag command : nbt.getList("Commands", Tag.TAG_COMPOUND)) {
                CommandType type = CommandType.valueOf(((CompoundTag) command).getString("Type"));
                DeviceCommand c = type.createEmpty(this);
                c.deserializeNBT((CompoundTag) command);
                commands.add(c);
                goalSelector.addGoal(0, c);
            }
        }
        if (nbt.contains("Owner", Tag.TAG_INT_ARRAY)) {
            owner = nbt.getUUID("Owner");
        }
        if (nbt.contains("Color", Tag.TAG_INT)) {
            entityData.set(COLOR, nbt.getInt("Color"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        ListTag commands = new ListTag();
        for (DeviceCommand command : this.commands) {
            commands.add(command.serializeNBT());
        }
        nbt.put("Commands", commands);
        if (owner != null) {
            nbt.putUUID("Owner", owner);
        }
        nbt.putInt("Color", entityData.get(COLOR));
    }
}

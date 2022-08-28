package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.registration.BlockRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LighthouseEntity extends FlyingDevice implements MenuProvider {

    private final ItemStackHandler inventory;
    private BlockPos light;

    public LighthouseEntity(EntityType<LighthouseEntity> type, Level world) {
        super(type, world);
        inventory = createInventory();
        light = null;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.MAX_HEALTH, 10);
    }

    public static ItemStackHandler createInventory() {
        return new ItemStackHandler(27);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> inventory).cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected ItemStack getDeviceItem() {
        return ItemRegistry.LIGHTHOUSE.get().getDefaultInstance();
    }

    @Override
    public void dropEquipment() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemEntity item = new ItemEntity(level, getX(), getY(), getZ(), inventory.extractItem(i, inventory.getSlotLimit(i), false));
            level.addFreshEntity(item);
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        InteractionResult ret = super.interactAt(player, vec, hand);
        if (player instanceof ServerPlayer && player.equals(getOwner())) {
            if (!player.isCrouching() && player.getItemInHand(hand).isEmpty()) {
                return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            } else {
                NetworkHooks.openGui((ServerPlayer) player, this, buf -> buf.writeVarInt(getId()));
                return InteractionResult.SUCCESS;
            }
        }
        return ret;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.BEACON_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource s) {
        return SoundEvents.BEACON_POWER_SELECT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.BEACON_DEACTIVATE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return !player.isSpectator() ? new LighthouseContainer(id, inv, this) : null;
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container." + TowerOfGod.MOD_ID + ".lighthouse_container");
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos pos = new BlockPos(position());
        if (!pos.equals(light)) {
            removeLight(light);
            light = null;
        }
        if (level.isEmptyBlock(pos) && light == null && isAlive()) {
            light = pos;
            level.setBlockAndUpdate(pos, BlockRegistry.LIGHT.get().defaultBlockState());
        }
    }

    @Override
    public void tickDeath() {
        super.tickDeath();
        removeLight(light);
    }

    private void removeLight(BlockPos pos) {
        if (light != null && level.getBlockState(pos).getBlock().equals(BlockRegistry.LIGHT.get())) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Inventory", Tag.TAG_COMPOUND)) {
            inventory.deserializeNBT(nbt.getCompound("Inventory"));
        }
        if (nbt.contains("Light", Tag.TAG_INT_ARRAY)) {
            int[] pos = nbt.getIntArray("Light");
            light = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        if (light != null) {
            nbt.putIntArray("Light", new int[]{light.getX(), light.getY(), light.getZ()});
        }
    }

    public static class LighthouseContainer extends AbstractContainerMenu {

        public final LighthouseEntity lighthouse;

        public LighthouseContainer(int id, Inventory player, LighthouseEntity lighthouse) {
            super(ContainerRegistry.LIGHTHOUSE.get(), id);
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 9; column++) {
                    addSlot(new SlotItemHandler(lighthouse.inventory, column + row * 9, 8 + column * 18, 11 + row * 18));
                }
            }
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 9; column++) {
                    addSlot(new Slot(player, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
                }
            }
            for (int index = 0; index < 9; index++) {
                addSlot(new Slot(player, index, 8 + index * 18, 142));
            }
            this.lighthouse = lighthouse;
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return playerIn.equals(lighthouse.getOwner());
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            ItemStack returnStack = ItemStack.EMPTY;
            final Slot slot = slots.get(index);
            if (slot.hasItem()) {
                final ItemStack slotStack = slot.getItem();
                returnStack = slotStack.copy();
                final int containerSlots = slots.size() - player.inventoryMenu.slots.size();
                if (index < containerSlots) {
                    if (!moveItemStackTo(slotStack, containerSlots, slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
                if (slotStack.getCount() == 0) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
                if (slotStack.getCount() == returnStack.getCount()) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, slotStack);
            }
            return returnStack;
        }

        public static class Factory implements IContainerFactory<LighthouseContainer> {
            @Nullable
            @Override
            public LighthouseContainer create(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
                LighthouseEntity entity = (LighthouseEntity) playerInv.player.level.getEntity(extraData.readVarInt());
                return entity != null ? new LighthouseContainer(windowId, playerInv, entity) : null;
            }
        }
    }

}

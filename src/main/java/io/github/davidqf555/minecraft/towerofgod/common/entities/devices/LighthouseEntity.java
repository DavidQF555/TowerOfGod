package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LighthouseEntity extends FlyingDevice implements INamedContainerProvider {

    private final ItemStackHandler inventory;
    private BlockPos light;

    public LighthouseEntity(World worldIn) {
        super(RegistryHandler.LIGHTHOUSE_ENTITY.get(), worldIn);
        inventory = createInventory();
        light = null;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.2)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2)
                .createMutableAttribute(Attributes.MAX_HEALTH, 10);
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
        return RegistryHandler.LIGHTHOUSE_ITEM.get().getDefaultInstance();
    }

    @Override
    public void dropInventory() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemEntity item = new ItemEntity(world, getPosX(), getPosY(), getPosZ(), inventory.extractItem(i, inventory.getSlotLimit(i), false));
            world.addEntity(item);
        }
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        ActionResultType ret = super.applyPlayerInteraction(player, vec, hand);
        if (player instanceof ServerPlayerEntity && player.equals(getOwner())) {
            if (!player.isSneaking() && player.getHeldItem(hand).isEmpty()) {
                return player.startRiding(this) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            } else {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> buf.writeVarInt(getEntityId()));
                return ActionResultType.SUCCESS;
            }
        }
        return ret;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_BEACON_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource s) {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_BEACON_DEACTIVATE;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return !player.isSpectator() ? new LighthouseContainer(id, inv, this) : null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container." + TowerOfGod.MOD_ID + ".lighthouse_container");
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos pos = new BlockPos(getPositionVec());
        if (!pos.equals(light)) {
            removeLight(light);
            light = null;
        }
        if (world.isAirBlock(pos) && light == null && isAlive()) {
            light = pos;
            world.setBlockState(pos, RegistryHandler.LIGHT_BLOCK.get().getDefaultState());
        }
    }

    @Override
    public void onDeathUpdate() {
        super.onDeathUpdate();
        removeLight(light);
    }

    private void removeLight(BlockPos pos) {
        if (light != null && world.getBlockState(pos).getBlock().equals(RegistryHandler.LIGHT_BLOCK.get())) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Inventory", Constants.NBT.TAG_COMPOUND)) {
            inventory.deserializeNBT(nbt.getCompound("Inventory"));
        }
        if (nbt.contains("Light", Constants.NBT.TAG_INT_ARRAY)) {
            int[] pos = nbt.getIntArray("Light");
            light = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        if (light != null) {
            nbt.putIntArray("Light", new int[]{light.getX(), light.getY(), light.getZ()});
        }
    }

    public static class LighthouseContainer extends Container {

        public final LighthouseEntity lighthouse;

        public LighthouseContainer(int id, PlayerInventory player, LighthouseEntity lighthouse) {
            super(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), id);
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
        public boolean canInteractWith(PlayerEntity playerIn) {
            return playerIn.equals(lighthouse.getOwner());
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity player, int index) {
            ItemStack returnStack = ItemStack.EMPTY;
            final Slot slot = inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                final ItemStack slotStack = slot.getStack();
                returnStack = slotStack.copy();
                final int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
                if (index < containerSlots) {
                    if (!mergeItemStack(slotStack, containerSlots, inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!mergeItemStack(slotStack, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
                if (slotStack.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
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
            public LighthouseContainer create(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
                LighthouseEntity entity = (LighthouseEntity) playerInv.player.world.getEntityByID(extraData.readVarInt());
                return entity != null ? new LighthouseContainer(windowId, playerInv, entity) : null;
            }
        }
    }

    public static class Factory implements EntityType.IFactory<LighthouseEntity> {
        @Override
        public LighthouseEntity create(@Nullable EntityType<LighthouseEntity> type, World world) {
            return new LighthouseEntity(world);
        }
    }
}

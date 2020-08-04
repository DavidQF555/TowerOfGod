package com.davidqf.minecraft.towerofgod.entities;

import java.util.Objects;
import java.util.concurrent.Callable;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LighthouseEntity extends FlyingDevice implements INamedContainerProvider {

    private static final int INVENTORY_SIZE = 27;
    private final IData data;
    private BlockPos light;

    public LighthouseEntity(World worldIn, @Nullable LivingEntity owner) {
        super(RegistryHandler.LIGHTHOUSE_ENTITY.get(), worldIn, owner);
        data = getCapability(DataProvider.capability).orElseThrow(NullPointerException::new);
        data.setInventory(new Inventory(INVENTORY_SIZE));
        light = null;
    }

    @Nonnull
    @Override
    public ActionResultType applyPlayerInteraction(@Nonnull PlayerEntity player, @Nonnull Vector3d vec, @Nonnull Hand hand) {
        ActionResultType ret = super.applyPlayerInteraction(player, vec, hand);
        if (getOwner() != null && player.equals(getOwner())) {
            openInventory(player);
            return ActionResultType.SUCCESS;
        }
        return ret;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return LighthouseEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233822_e_, 3)
                .func_233815_a_(Attributes.field_233821_d_, 3)
                .func_233815_a_(Attributes.field_233818_a_, 10);
    }

    @Nonnull
    private Inventory getInventory() {
        return data.getInventory();
    }

    @Override
    public void dropInventory() {
        for (ItemStack item : getInventory().func_233543_f_()) {
            ItemEntity en = new ItemEntity(world, getPosX(), getPosY(), getPosZ(), item);
            world.addEntity(en);
        }
        data.getInventory().clear();
    }

    public void openInventory(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> buf.writeVarInt(getEntityId()));
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_BEACON_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(@Nonnull DamageSource s) {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_BEACON_DEACTIVATE;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory inv, PlayerEntity player) {
        return !player.isSpectator() ? new LighthouseContainer(id, inv, this) : null;
    }

    @Nonnull
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

    private void removeLight(@Nonnull BlockPos pos) {
        if (light != null && world.getBlockState(pos).getBlock().equals(RegistryHandler.LIGHT_BLOCK.get())) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public static class LighthouseContainer extends Container {

        private final LighthouseEntity en;

        public LighthouseContainer(int id, PlayerInventory player, LighthouseEntity en) {
            super(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), id);
            Inventory inv = en.getInventory();
            for (int k = 0; k < 3; ++k) {
                for (int l = 0; l < 9; ++l) {
                    this.addSlot(new Slot(inv, l + k * 9, 8 + l * 18, 11 + k * 18));
                }
            }

            for (int i1 = 0; i1 < 3; ++i1) {
                for (int k1 = 0; k1 < 9; ++k1) {
                    this.addSlot(new Slot(player, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
                }
            }

            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(player, j1, 8 + j1 * 18, 142));
            }
            this.en = en;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return playerIn.equals(en.getOwner());
        }

        @Nonnull
        @Override
        public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int index) {
            ItemStack returnStack = ItemStack.EMPTY;
            final Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                final ItemStack slotStack = slot.getStack();
                returnStack = slotStack.copy();
                final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
                if (index < containerSlots) {
                    if (!mergeItemStack(slotStack, containerSlots, this.inventorySlots.size(), true)) {
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

            @Override
            public LighthouseContainer create(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
                LighthouseEntity entity = (LighthouseEntity) playerInv.player.world.getEntityByID(extraData.readVarInt());
                return entity != null ? new LighthouseContainer(windowId, playerInv, entity) : null;
            }
        }
    }

    public static class LighthouseScreen extends ContainerScreen<LighthouseContainer> {

        private static final ResourceLocation GUI = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/container/lighthouse_container.png");

        public LighthouseScreen(LighthouseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn);
            field_238743_q_ -= 4;
            field_238745_s_ += 2;
        }

        @Override
        public void func_230430_a_(@Nonnull MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            this.func_230446_a_(p_230430_1_);
            super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            this.func_230459_a_(p_230430_1_, p_230430_2_, p_230430_3_);
        }

        @Deprecated
        @Override
        protected void func_230450_a_(@Nonnull MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (field_230706_i_ != null) {
                field_230706_i_.getTextureManager().bindTexture(GUI);
            }
            int i = (this.field_230708_k_ - this.xSize) / 2;
            int j = (this.field_230709_l_ - this.ySize) / 2;
            this.func_238474_b_(p_230450_1_, i, j, 0, 0, this.xSize, this.ySize);
        }

        public static class Factory implements IScreenFactory<LighthouseContainer, LighthouseScreen> {
            @Nonnull
            @Override
            public LighthouseScreen create(@Nonnull LighthouseContainer con, @Nonnull PlayerInventory inv, @Nonnull ITextComponent text) {
                return new LighthouseScreen(con, inv, text);
            }
        }
    }

    public static class Factory implements EntityType.IFactory<LighthouseEntity> {
        @Nonnull
        @Override
        public LighthouseEntity create(@Nullable EntityType<LighthouseEntity> type, @Nonnull World world) {
            return new LighthouseEntity(world, null);
        }
    }

    public interface IData {

        Inventory getInventory();

        void setInventory(Inventory inv);

    }

    public static class Data implements IData {

        private Inventory inv;

        public Data() {
            inv = new Inventory(INVENTORY_SIZE);
        }

        @Override
        public Inventory getInventory() {
            return inv;
        }

        @Override
        public void setInventory(Inventory inv) {
            this.inv = inv;
        }

        public static class Factory implements Callable<IData> {
            @Override
            public IData call() {
                return new Data();
            }
        }
    }

    public static class DataProvider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(IData.class)
        public static Capability<IData> capability = null;
        private final LazyOptional<IData> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return cap == capability ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return capability.getStorage().writeNBT(capability, instance.orElseThrow(NullPointerException::new), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            capability.getStorage().readNBT(capability, instance.orElseThrow(NullPointerException::new), null, nbt);
        }
    }

    public static class DataStorage implements Capability.IStorage<IData> {

        @Override
        public INBT writeNBT(Capability<IData> capability, IData instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.put("inventory", instance.getInventory().write());
            return tag;
        }

        @Override
        public void readNBT(Capability<IData> capability, IData instance, Direction side, INBT nbt) {
            CompoundNBT tag = (CompoundNBT) nbt;
            Inventory inv = new Inventory(INVENTORY_SIZE);
            inv.read(tag.getList("inventory", Constants.NBT.TAG_COMPOUND));
            instance.setInventory(inv);
        }
    }
}

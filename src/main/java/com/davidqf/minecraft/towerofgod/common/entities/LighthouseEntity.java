package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LighthouseEntity extends FlyingDevice implements INamedContainerProvider {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".lighthouseentity";
    private static final int INVENTORY_SIZE = 27;
    private Inventory inventory;
    private BlockPos light;

    public LighthouseEntity(World worldIn) {
        super(RegistryHandler.LIGHTHOUSE_ENTITY.get(), worldIn);
        inventory = new Inventory(INVENTORY_SIZE);
        light = null;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.2)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2)
                .createMutableAttribute(Attributes.MAX_HEALTH, 10);
    }

    private Inventory getInventory() {
        return inventory;
    }

    @Override
    public void dropInventory() {
        for (ItemStack item : inventory.func_233543_f_()) {
            ItemEntity en = new ItemEntity(world, getPosX(), getPosY(), getPosZ(), item);
            world.addEntity(en);
        }
        inventory.clear();
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        ActionResultType ret = super.applyPlayerInteraction(player, vec, hand);
        if (getOwner() != null && player.equals(getOwner())) {
            openInventory(player);
            return ActionResultType.SUCCESS;
        }
        return ret;
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
        inventory = new Inventory(INVENTORY_SIZE);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT lighthouse = (CompoundNBT) nbt.get(TAG_KEY);
            if (lighthouse != null) {
                inventory.read(lighthouse.getList("Inventory", Constants.NBT.TAG_COMPOUND));
                if (lighthouse.contains("X", Constants.NBT.TAG_COMPOUND)) {
                    light = new BlockPos(lighthouse.getInt("X"), lighthouse.getInt("Y"), lighthouse.getInt("Z"));
                }
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        CompoundNBT lighthouse = new CompoundNBT();
        lighthouse.put("Inventory", inventory.write());
        if (light != null) {
            lighthouse.putInt("X", light.getX());
            lighthouse.putInt("Y", light.getY());
            lighthouse.putInt("Z", light.getZ());
            nbt.put(TAG_KEY, lighthouse);
        }
    }

    public static class LighthouseContainer extends Container {

        private final LighthouseEntity en;

        public LighthouseContainer(int id, PlayerInventory player, LighthouseEntity en) {
            super(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), id);
            Inventory inv = en.getInventory();
            for (int k = 0; k < 3; ++k) {
                for (int l = 0; l < 9; ++l) {
                    addSlot(new Slot(inv, l + k * 9, 8 + l * 18, 11 + k * 18));
                }
            }

            for (int i1 = 0; i1 < 3; ++i1) {
                for (int k1 = 0; k1 < 9; ++k1) {
                    addSlot(new Slot(player, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
                }
            }

            for (int j1 = 0; j1 < 9; ++j1) {
                addSlot(new Slot(player, j1, 8 + j1 * 18, 142));
            }
            this.en = en;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return playerIn.equals(en.getOwner());
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

    public static class LighthouseScreen extends ContainerScreen<LighthouseContainer> {

        private static final ResourceLocation GUI = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/container/lighthouse_container.png");

        public LighthouseScreen(LighthouseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn);
            titleY -= 4;
            playerInventoryTitleY += 2;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            renderBackground(matrixStack);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            func_230459_a_(matrixStack, mouseX, mouseY);
        }

        @Deprecated
        @Override
        protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
            RenderSystem.color4f(1, 1, 1, 1);
            if (minecraft != null) {
                minecraft.getTextureManager().bindTexture(GUI);
            }
            int i = (width - xSize) / 2;
            int j = (height - ySize) / 2;
            blit(matrixStack, i, j, 0, 0, xSize, ySize);
        }

        public static class Factory implements IScreenFactory<LighthouseContainer, LighthouseScreen> {
            @Override
            public LighthouseScreen create(LighthouseContainer con, PlayerInventory inv, ITextComponent text) {
                return new LighthouseScreen(con, inv, text);
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

package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Manifest extends ShinsuTechniqueInstance {

    private static final int DURATION = 2400;

    public Manifest(LivingEntity user, int level) {
        super(ShinsuTechnique.MANIFEST, user, level, DURATION);
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        IShinsuStats stats = IShinsuStats.get(user);
        ItemStack item = stats.getShape().createItem();
        CompoundNBT child = item.getOrCreateChildTag(TowerOfGod.MOD_ID);
        child.putUniqueId("Technique", getID());
        child.putString("Quality", stats.getQuality().name());
        IItemHandler inventory = user.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        ItemHandlerHelper.insertItem(inventory, item, false);
        super.onUse(world);
    }

    @Override
    public void tick(ServerWorld world) {
        boolean contains = false;
        IItemHandler inventory = getUser(world).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            CompoundNBT nbt = slot.getChildTag(TowerOfGod.MOD_ID);
            if (!slot.isEmpty() && nbt != null && getID().equals(nbt.getUniqueId("Technique"))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            remove(world);
        }
        super.tick(world);
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.Builder<Manifest> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public Manifest build(LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return new Manifest(user, level);
        }

        @Nonnull
        @Override
        public Manifest emptyBuild() {
            return new Manifest(null, 0);
        }

        @Override
        public boolean canCast(ShinsuTechnique technique, LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return ShinsuTechnique.Builder.super.canCast(technique, user, level, target, dir) && IShinsuStats.get(user).getShape() != ShinsuShape.NONE;
        }

        @Override
        public int getShinsuUse() {
            return shinsu;
        }

        @Override
        public int getBaangUse() {
            return baangs;
        }
    }
}

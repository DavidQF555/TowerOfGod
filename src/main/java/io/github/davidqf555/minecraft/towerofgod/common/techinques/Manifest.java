package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class Manifest extends ShinsuTechniqueInstance {

    public Manifest(LivingEntity user, int level) {
        super(null, user, level, 1);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.MANIFEST;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        IShinsuStats stats = IShinsuStats.get(user);
        ItemStack item = stats.getShape().createItem();
        item.getOrCreateChildTag(TowerOfGod.MOD_ID).putUniqueId("Technique", getID());
        ShinsuQuality.setQuality(item, stats.getQuality());
        IItemHandler inventory = user.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.isItemValid(i, item)) {
                item = inventory.insertItem(i, item, false);
                if (item.isEmpty()) {
                    break;
                }
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void tick(ServerWorld world) {
        boolean contains = false;
        IItemHandler inventory = getUser(world).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        UUID id = getID();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            CompoundNBT tag = slot.getChildTag(TowerOfGod.MOD_ID);
            if (!slot.isEmpty() && tag != null && tag.contains("Technique", Constants.NBT.TAG_INT_ARRAY) && id.equals(tag.getUniqueId("Technique"))) {
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
    public static class Builder implements ShinsuTechnique.IBuilder<Manifest> {

        @Override
        public Manifest build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return IShinsuStats.get(user).getShape() == ShinsuShape.NONE ? null : new Manifest(user, level);
        }

        @Nonnull
        @Override
        public Manifest emptyBuild() {
            return new Manifest(null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.MANIFEST;
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class Manifest extends ShinsuTechniqueInstance {

    public Manifest(LivingEntity user, int level) {
        super(user, level);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.MANIFEST;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        ShinsuStats stats = ShinsuStats.get(user);
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

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<Manifest> {

        @Override
        public Either<Manifest, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return ShinsuStats.get(user).getShape() == ShinsuShape.NONE ? Either.right(ErrorMessages.REQUIRES_SHAPE) : Either.left(new Manifest(user, level));
        }

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

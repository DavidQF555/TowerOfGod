package io.github.davidqf555.minecraft.towerofgod.common.techinques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.ShapeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.TypeLevelRequirement;
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

    private ShinsuShape shape;
    private ShinsuQuality quality;

    public Manifest(LivingEntity user, ShinsuShape shape, ShinsuQuality quality) {
        super(user);
        this.shape = shape;
        this.quality = quality;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.MANIFEST;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        ItemStack item = shape.createItem();
        item.getOrCreateChildTag(TowerOfGod.MOD_ID).putUniqueId("Technique", getID());
        ShinsuQuality.setQuality(item, quality);
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
    public void periodicTick(ServerWorld world, int period) {
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
        super.periodicTick(world, period);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            shape = ShinsuShape.valueOf(nbt.getString("Shape"));
        }
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            quality = ShinsuQuality.valueOf(nbt.getString("Quality"));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putString("Shape", shape.name());
        nbt.putString("Quality", quality.name());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<Manifest> {

        @Override
        public Either<Manifest, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            ShinsuStats stats = ShinsuStats.get(user);
            return Either.left(new Manifest(user, stats.getShape(), stats.getQuality()));
        }

        @Override
        public Manifest blankCreate() {
            return new Manifest(null, ShinsuShape.NONE, ShinsuQuality.NONE);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.MANIFEST;
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new ShapeRequirement()};
        }
    }
}

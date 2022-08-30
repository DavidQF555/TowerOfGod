package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class Manifest extends ShinsuTechniqueInstance {

    private ShinsuShape shape;
    private ShinsuAttribute attribute;

    public Manifest(LivingEntity user, ShinsuShape shape, @Nullable ShinsuAttribute attribute) {
        super(user);
        this.shape = shape;
        this.attribute = attribute;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.MANIFEST.get();
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        ItemStack item = shape.getItem();
        item.getOrCreateTagElement(TowerOfGod.MOD_ID).putUUID("Technique", getID());
        ShinsuAttribute.setAttribute(item, attribute);
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
    public void tick(ServerLevel world) {
        boolean contains = false;
        IItemHandler inventory = getUser(world).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        UUID id = getID();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            CompoundTag tag = slot.getTagElement(TowerOfGod.MOD_ID);
            if (!slot.isEmpty() && tag != null && tag.contains("Technique", Tag.TAG_INT_ARRAY) && id.equals(tag.getUUID("Technique"))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Shape", Tag.TAG_STRING)) {
            shape = ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape")));
        }
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            attribute = ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute")));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("Shape", shape.getId().toString());
        if (attribute != null) {
            nbt.putString("Attribute", attribute.getId().toString());
        }
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<Manifest> {

        @Override
        public Either<Manifest, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            ShinsuStats stats = ShinsuStats.get(user);
            return Either.left(new Manifest(user, stats.getShape(), stats.getAttribute()));
        }

        @Override
        public Manifest blankCreate() {
            return new Manifest(null, null, null);
        }

    }
}

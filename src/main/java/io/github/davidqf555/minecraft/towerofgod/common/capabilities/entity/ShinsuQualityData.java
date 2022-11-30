package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class ShinsuQualityData implements INBTSerializable<CompoundTag> {

    public static final Capability<ShinsuQualityData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private ShinsuAttribute attribute;
    private ShinsuShape shape;

    public static ShinsuQualityData get(Entity entity) {
        return entity.getCapability(CAPABILITY).orElseGet(ShinsuQualityData::new);
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    @Nullable
    public ShinsuShape getShape() {
        return shape;
    }

    public void setShape(@Nullable ShinsuShape shape) {
        this.shape = shape;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            tag.putString("Attribute", attribute.getId().toString());
        }
        ShinsuShape shape = getShape();
        if (shape != null) {
            tag.putString("Shape", shape.getId().toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
        if (nbt.contains("Shape", Tag.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
    }

}

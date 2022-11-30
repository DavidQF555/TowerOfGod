package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class ShinsuQualityData implements INBTSerializable<CompoundNBT> {

    @CapabilityInject(ShinsuQualityData.class)
    public static Capability<ShinsuQualityData> capability = null;
    private ShinsuAttribute attribute;
    private ShinsuShape shape;

    public static ShinsuQualityData get(Entity entity) {
        return entity.getCapability(capability).orElseGet(ShinsuQualityData::new);
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            tag.putString("Attribute", attribute.getRegistryName().toString());
        }
        ShinsuShape shape = getShape();
        if (shape != null) {
            tag.putString("Shape", shape.getRegistryName().toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Attribute", Constants.NBT.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
    }

}

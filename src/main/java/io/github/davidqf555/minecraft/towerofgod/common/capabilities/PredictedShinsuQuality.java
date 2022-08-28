package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class PredictedShinsuQuality implements INBTSerializable<CompoundNBT> {

    @CapabilityInject(PredictedShinsuQuality.class)
    public static Capability<PredictedShinsuQuality> capability = null;
    private ShinsuShape shape;
    private ShinsuAttribute attribute;

    public static PredictedShinsuQuality get(PlayerEntity player) {
        return player.getCapability(capability).orElseGet(PredictedShinsuQuality::new);
    }

    @Nullable
    public ShinsuShape getShape() {
        return shape;
    }

    public void setShape(@Nullable ShinsuShape shape) {
        this.shape = shape;
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ShinsuShape shape = getShape();
        if (shape != null) {
            nbt.putString("Shape", shape.getRegistryName().toString());
        }
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            nbt.putString("Attribute", attribute.getRegistryName().toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
        if (nbt.contains("Attribute", Constants.NBT.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class PredictedShinsuQuality implements INBTSerializable<CompoundTag> {

    public static final Capability<PredictedShinsuQuality> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private ShinsuShape shape;
    private ShinsuAttribute attribute;

    public static PredictedShinsuQuality get(Player player) {
        return player.getCapability(CAPABILITY).orElseGet(PredictedShinsuQuality::new);
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
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
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Shape", Tag.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
    }

}

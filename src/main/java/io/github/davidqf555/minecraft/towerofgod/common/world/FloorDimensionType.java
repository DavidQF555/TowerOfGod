package io.github.davidqf555.minecraft.towerofgod.common.world;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;

import java.util.OptionalLong;

public class FloorDimensionType extends DimensionType {

    protected FloorDimensionType(FloorProperty property, double coordinateScale, ResourceLocation effects, float ambientLight) {
        super(property.isTimeFixed() ? OptionalLong.of(property.getTime()) : OptionalLong.empty(), !property.hasCeiling(), property.hasCeiling(), false, true, coordinateScale, false, false, true, true, true, property.hasCeiling() ? 128 : 256, property.hasCeiling() ? FuzzedBiomeMagnifier.INSTANCE : ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), effects, ambientLight);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FloorProperty {

    public static Codec<FloorProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Attribute.CODEC.fieldOf("primaryAttribute").forGetter(property -> property.primaryAttribute),
            Codec.list(Attribute.CODEC).fieldOf("attributes").forGetter(property -> new ArrayList<>(property.attributes)),
            Codec.list(Bound.CODEC).fieldOf("bounds").forGetter(property -> new ArrayList<>(property.bounds)),
            Time.CODEC.fieldOf("time").forGetter(property -> property.time),
            Codec.FLOAT.fieldOf("shinsuDensity").forGetter(property -> property.shinsuDensity)
    ).apply(builder, builder.stable((primaryAttribute, attributes, bounds, time, shinsuDensity) -> new FloorProperty(primaryAttribute, EnumSet.copyOf(attributes), bounds.isEmpty() ? EnumSet.noneOf(Bound.class) : EnumSet.copyOf(bounds), time, shinsuDensity))));

    private final Attribute primaryAttribute;
    private final Set<Attribute> attributes;
    private final Set<Bound> bounds;
    private final Time time;
    private final float shinsuDensity;

    public FloorProperty(Attribute primaryAttribute, Set<Attribute> attributes, Set<Bound> bounds, Time time, float shinsuDensity) {
        this.primaryAttribute = primaryAttribute;
        this.attributes = attributes;
        this.bounds = bounds;
        this.time = time;
        this.shinsuDensity = shinsuDensity;
    }

    public Attribute getPrimaryAttribute() {
        return primaryAttribute;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public List<Pair<Supplier<Biome>, Biome.Attributes>> getBiomeAttributesList(Registry<Biome> lookup) {
        List<Pair<Supplier<Biome>, Biome.Attributes>> biomes = new ArrayList<>();
        getBiomes(lookup).forEach(supplier -> {
            Biome biome = supplier.get();
            biomes.add(Pair.of(supplier, new Biome.Attributes(biome.getTemperature(), biome.getDownfall(), 0, 0, 0)));
        });
        return biomes;
    }

    private List<Supplier<Biome>> getBiomes(Registry<Biome> lookup) {
        Predicate<Biome> condition = biome -> false;
        Set<FloorProperty.Attribute> attributes = getAttributes();
        if (attributes.isEmpty()) {
            condition = biome -> true;
        } else {
            for (FloorProperty.Attribute attribute : attributes) {
                condition = condition.or(attribute.getConditions());
            }
        }
        List<Supplier<Biome>> biomes = new ArrayList<>();
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : lookup.getEntries()) {
            if (condition.test(entry.getValue())) {
                biomes.add(() -> lookup.getOrThrow(entry.getKey()));
            }
        }
        return biomes;
    }

    public BlockState getBlockState() {
        return primaryAttribute.getBlockState();
    }

    public BlockState getFluid() {
        return primaryAttribute.getFluid();
    }

    public boolean isUltrawarm() {
        return primaryAttribute.isUltrawarm();
    }

    public boolean isPiglinSafe() {
        return primaryAttribute.piglinSafe;
    }

    public boolean hasCeiling() {
        return bounds.contains(Bound.CEILING);
    }

    public boolean hasFloor() {
        return bounds.contains(Bound.FLOOR);
    }

    public boolean isTimeFixed() {
        return time.isFixed();
    }

    public long getTime() {
        return time.getTime();
    }

    public float getShinsuDensity() {
        return shinsuDensity;
    }

    public enum Attribute {

        NETHER(Blocks.NETHERRACK::getDefaultState, Blocks.LAVA::getDefaultState, true, true, biome -> biome.getCategory() == Biome.Category.NETHER),
        COLD(Blocks.STONE::getDefaultState, Blocks.WATER::getDefaultState, false, false, biome -> biome.getCategory() == Biome.Category.ICY || biome.getCategory() == Biome.Category.TAIGA),
        END(Blocks.END_STONE::getDefaultState, Blocks.AIR::getDefaultState, false, false, biome -> biome.getCategory() == Biome.Category.THEEND),
        DESERT(Blocks.SANDSTONE::getDefaultState, Blocks.WATER::getDefaultState, false, false, biome -> biome.getCategory() == Biome.Category.DESERT),
        MUSHROOM(Blocks.STONE::getDefaultState, Blocks.WATER::getDefaultState, false, false, biome -> biome.getCategory() == Biome.Category.MUSHROOM);

        public static final Codec<Attribute> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.fieldOf("name").stable().forGetter(Enum::name)
        ).apply(builder, builder.stable(Attribute::valueOf)));
        private final Supplier<BlockState> block;
        private final Supplier<BlockState> fluid;
        private final boolean ultrawarm;
        private final boolean piglinSafe;
        private final Predicate<Biome> conditions;

        Attribute(Supplier<BlockState> block, Supplier<BlockState> fluid, boolean ultrawarm, boolean piglinSafe, Predicate<Biome> conditions) {
            this.block = block;
            this.fluid = fluid;
            this.ultrawarm = ultrawarm;
            this.piglinSafe = piglinSafe;
            this.conditions = conditions;
        }

        public BlockState getBlockState() {
            return block.get();
        }

        public BlockState getFluid() {
            return fluid.get();
        }

        public boolean isUltrawarm() {
            return ultrawarm;
        }

        public boolean isPiglinSafe() {
            return piglinSafe;
        }

        public Predicate<Biome> getConditions() {
            return conditions;
        }
    }

    public enum Bound {

        FLOOR(),
        CEILING();

        public static final Codec<Bound> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.fieldOf("name").stable().forGetter(Enum::name)
        ).apply(builder, builder.stable(Bound::valueOf)));
    }

    public enum Time {

        DYNAMIC(-1),
        DAWN(0),
        NOON(6000),
        SUNSET(12000),
        MIDNIGHT(18000);

        public static final Codec<Time> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.fieldOf("name").stable().forGetter(Enum::name)
        ).apply(builder, builder.stable(Time::valueOf)));
        private final long time;

        Time(long time) {
            this.time = time;
        }

        public boolean isFixed() {
            return time > 0;
        }

        public long getTime() {
            return time;
        }
    }
}

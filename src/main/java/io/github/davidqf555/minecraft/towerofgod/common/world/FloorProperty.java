package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FloorProperty {

    public static Codec<FloorProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("level").forGetter(property -> property.level),
            Codec.list(Codec.STRING).fieldOf("type").forGetter(property -> property.types.stream().map(BiomeDictionary.Type::getName).collect(Collectors.toList())),
            Codec.list(Bound.CODEC).fieldOf("bounds").forGetter(property -> new ArrayList<>(property.bounds)),
            Time.CODEC.fieldOf("time").forGetter(property -> property.time),
            Codec.FLOAT.fieldOf("shinsuDensity").forGetter(property -> property.shinsuDensity)
    ).apply(builder, builder.stable((level, types, bounds, time, shinsuDensity) -> new FloorProperty(level, types.stream().map(BiomeDictionary.Type::getType).collect(Collectors.toSet()), bounds.isEmpty() ? EnumSet.noneOf(Bound.class) : EnumSet.copyOf(bounds), time, shinsuDensity))));

    private final int level;
    private final Set<BiomeDictionary.Type> types;
    private final Set<Bound> bounds;
    private final Time time;
    private final float shinsuDensity;

    public FloorProperty(int level, Set<BiomeDictionary.Type> types, Set<Bound> bounds, Time time, float shinsuDensity) {
        this.level = level;
        this.types = types;
        this.bounds = bounds;
        this.time = time;
        this.shinsuDensity = shinsuDensity;
    }

    public int getLevel() {
        return level;
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
        List<Supplier<Biome>> biomes = new ArrayList<>();
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : lookup.getEntries()) {
            RegistryKey<Biome> key = entry.getKey();
            for (BiomeDictionary.Type type : types) {
                if (BiomeDictionary.hasType(key, type)) {
                    biomes.add(() -> lookup.getOrThrow(entry.getKey()));
                    break;
                }
            }
        }
        return biomes;
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

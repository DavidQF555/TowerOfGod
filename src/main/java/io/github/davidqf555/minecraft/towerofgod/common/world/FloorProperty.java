package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class FloorProperty extends WorldSavedData {

    private static final String NAME = TowerOfGod.MOD_ID + "_FloorProperty";
    private final Set<BiomeDictionary.Type> types;
    private final Set<Bound> bounds;
    private int level;
    private Time time;
    private float shinsuDensity;

    public FloorProperty(int level, Set<BiomeDictionary.Type> types, Set<Bound> bounds, Time time, float shinsuDensity) {
        super(NAME);
        this.level = level;
        this.types = types;
        this.bounds = bounds;
        this.time = time;
        this.shinsuDensity = shinsuDensity;
    }

    public static void set(ServerWorld world, FloorProperty property) {
        world.getSavedData().set(property);
    }

    @Nullable
    public static FloorProperty get(ServerWorld world) {
        return world.getSavedData().get(() -> new FloorProperty(1, new HashSet<>(), new HashSet<>(), Time.DYNAMIC, 1), NAME);
    }

    public int getLevel() {
        return level;
    }

    public List<Pair<Biome.Attributes, Supplier<Biome>>> getBiomeAttributesList(Registry<Biome> lookup) {
        List<Pair<Biome.Attributes, Supplier<Biome>>> biomes = new ArrayList<>();
        getBiomes(lookup).forEach(supplier -> {
            Biome biome = supplier.get();
            biomes.add(Pair.of(new Biome.Attributes(biome.getTemperature(), biome.getDownfall(), 0, 0, 0), supplier));
        });
        return biomes;
    }

    public List<Supplier<Biome>> getBiomes(Registry<Biome> lookup) {
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

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
        if (nbt.contains("Types", Constants.NBT.TAG_LIST)) {
            for (INBT data : nbt.getList("Types", Constants.NBT.TAG_STRING)) {
                String name = data.getString();
                if (BiomeDictionary.Type.getAll().stream().anyMatch(type -> type.getName().equals(name))) {
                    types.add(BiomeDictionary.Type.getType(name));
                }
            }
        }
        if (nbt.contains("Bounds", Constants.NBT.TAG_LIST)) {
            for (INBT name : nbt.getList("Bounds", Constants.NBT.TAG_STRING)) {
                bounds.add(Bound.valueOf(name.getString()));
            }
        }
        if (nbt.contains("Time", Constants.NBT.TAG_STRING)) {
            time = Time.valueOf(nbt.getString("Time"));
        }
        if (nbt.contains("Density", Constants.NBT.TAG_FLOAT)) {
            shinsuDensity = nbt.getFloat("Density");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putInt("Level", getLevel());
        ListNBT types = new ListNBT();
        for (BiomeDictionary.Type type : this.types) {
            types.add(StringNBT.valueOf(type.getName()));
        }
        nbt.put("Types", types);
        ListNBT bounds = new ListNBT();
        for (Bound bound : this.bounds) {
            bounds.add(StringNBT.valueOf(bound.name()));
        }
        nbt.put("Bounds", bounds);
        nbt.putString("Time", time.name());
        nbt.putFloat("Density", getShinsuDensity());
        return nbt;
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

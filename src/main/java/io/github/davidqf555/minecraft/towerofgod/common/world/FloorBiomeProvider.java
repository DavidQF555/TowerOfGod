package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.MaxMinNoiseMixer;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
public class FloorBiomeProvider extends BiomeProvider {

    public static final Codec<FloorBiomeProvider> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.LONG.fieldOf("seed").stable().forGetter(provider -> provider.seed),
            FloorProperty.CODEC.fieldOf("property").forGetter(provider -> provider.property),
            RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).fieldOf("lookup").forGetter(provider -> provider.lookup)
    ).apply(builder, builder.stable(FloorBiomeProvider::new)));

    private final long seed;
    private final Registry<Biome> lookup;
    private final MaxMinNoiseMixer temperatureMixer;
    private final MaxMinNoiseMixer humidityMixer;
    private final MaxMinNoiseMixer altitudeMixer;
    private final MaxMinNoiseMixer weirdnessMixer;
    private final FloorProperty property;
    private final List<Pair<Supplier<Biome>, Biome.Attributes>> attributes;

    protected FloorBiomeProvider(long seed, FloorProperty property, Registry<Biome> lookup) {
        super(property.getBiomes(lookup).stream().map(Supplier::get).collect(Collectors.toList()));
        this.seed = seed;
        this.property = property;
        this.lookup = lookup;
        temperatureMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed), -7, new DoubleArrayList(ImmutableList.of(1.0, 1.0)));
        humidityMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 1), -7, new DoubleArrayList(ImmutableList.of(1.0, 1.0)));
        altitudeMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 2), -7, new DoubleArrayList(ImmutableList.of(1.0, 1.0)));
        weirdnessMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 3), -7, new DoubleArrayList(ImmutableList.of(1.0, 1.0)));
        attributes = property.getBiomeAttributesList(lookup);
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return new FloorBiomeProvider(seed, property, lookup);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        Biome.Attributes attributes = new Biome.Attributes((float) temperatureMixer.func_237211_a_(x, y, z), (float) humidityMixer.func_237211_a_(x, y, z), (float) altitudeMixer.func_237211_a_(x, y, z), (float) weirdnessMixer.func_237211_a_(x, y, z), 0);
        return this.attributes.stream()
                .min(Comparator.comparing(pair -> pair.getSecond().getAttributeDifference(attributes)))
                .map(Pair::getFirst)
                .map(Supplier::get)
                .orElse(BiomeRegistry.THE_VOID);
    }

    public FloorProperty getProperty() {
        return property;
    }
}

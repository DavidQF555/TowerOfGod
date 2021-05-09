package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.Blockreader;
import net.minecraft.world.Dimension;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FloorChunkGenerator extends ChunkGenerator {

    public static final Codec<FloorChunkGenerator> CODEC = RecordCodecBuilder.create(p_236091_0_ -> p_236091_0_.group(
            BiomeProvider.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeProvider),
            Codec.LONG.fieldOf("seed").stable().forGetter(gen -> gen.seed),
            DimensionSettings.DIMENSION_SETTINGS_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
    ).apply(p_236091_0_, p_236091_0_.stable(FloorChunkGenerator::new)));
    private static final float[] BEARD_KERNEL = Util.make(new float[13824], arr -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    arr[i * 24 * 24 + j * 24 + k] = (float) calculateContribution(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final float[] BIOME_WEIGHTS = Util.make(new float[25], arr -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10 / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                arr[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private static final Function<DimensionSettings, Boolean> MOB_GENERATION_DISABLED = FloorDimensionsHelper.getInstanceField(DimensionSettings.class, "disableMobGeneration");
    private static final Function<NoiseChunkGenerator, Supplier<DimensionSettings>> SETTINGS = FloorDimensionsHelper.getInstanceField(NoiseChunkGenerator.class, "field_236080_h_");
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final Map<Biome, Pair<BlockState, BlockState>> defaults = new HashMap<>();
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final Supplier<DimensionSettings> settings;
    private final int verticalNoiseGranularity;
    private final int horizontalNoiseGranularity;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final OctavesNoiseGenerator minNoise;
    private final OctavesNoiseGenerator maxNoise;
    private final OctavesNoiseGenerator mainNoise;
    private final INoiseGenerator surfaceNoise;
    private final OctavesNoiseGenerator depthNoise;
    private final SimplexNoiseGenerator islandNoise;
    private final long seed;
    private final int max;

    public FloorChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settings) {
        this(provider, provider, seed, settings);
    }

    private FloorChunkGenerator(BiomeProvider provider1, BiomeProvider provider2, long seed, Supplier<DimensionSettings> settings) {
        super(provider1, provider2, settings.get().getStructures(), seed);
        this.seed = seed;
        DimensionSettings dimensionsettings = settings.get();
        this.settings = settings;
        NoiseSettings noisesettings = dimensionsettings.getNoise();
        max = noisesettings.func_236169_a_();
        verticalNoiseGranularity = noisesettings.func_236175_f_() * 4;
        horizontalNoiseGranularity = noisesettings.func_236174_e_() * 4;
        defaultBlock = dimensionsettings.getDefaultBlock();
        defaultFluid = dimensionsettings.getDefaultFluid();
        noiseSizeX = 16 / horizontalNoiseGranularity;
        noiseSizeY = noisesettings.func_236169_a_() / verticalNoiseGranularity;
        noiseSizeZ = 16 / horizontalNoiseGranularity;
        SharedSeedRandom random = new SharedSeedRandom(seed);
        minNoise = new OctavesNoiseGenerator(random, IntStream.rangeClosed(-15, 0));
        maxNoise = new OctavesNoiseGenerator(random, IntStream.rangeClosed(-15, 0));
        mainNoise = new OctavesNoiseGenerator(random, IntStream.rangeClosed(-7, 0));
        surfaceNoise = noisesettings.func_236178_i_() ? new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(random, IntStream.rangeClosed(-3, 0));
        random.skip(2620);
        depthNoise = new OctavesNoiseGenerator(random, IntStream.rangeClosed(-15, 0));
        if (noisesettings.func_236180_k_()) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
            sharedseedrandom.skip(17292);
            islandNoise = new SimplexNoiseGenerator(sharedseedrandom);
        } else {
            islandNoise = null;
        }

    }

    private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
        int i = p_222556_0_ + 12;
        int j = p_222556_1_ + 12;
        int k = p_222556_2_ + 12;
        if (i >= 0 && i < 24) {
            if (j >= 0 && j < 24) {
                return k >= 0 && k < 24 ? (double) BEARD_KERNEL[k * 24 * 24 + i * 24 + j] : 0;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private static double calculateContribution(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
        double d0 = p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_;
        double d1 = (double) p_222554_1_ + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(Math.E, -(d2 / 16 + d0 / 16));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2 + d0 / 2) / 2;
        return d4 * d3;
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long p_230349_1_) {
        return new FloorChunkGenerator(biomeProvider.getBiomeProvider(p_230349_1_), p_230349_1_, settings);
    }

    private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
        double d0 = 0;
        double d1 = 0;
        double d2 = 0;
        double d3 = 1;
        for (int i = 0; i < 16; ++i) {
            double d4 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_4_ * d3);
            double d5 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_6_ * d3);
            double d6 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_4_ * d3);
            double d7 = p_222552_6_ * d3;
            ImprovedNoiseGenerator improvednoisegenerator = minNoise.getOctave(i);
            if (improvednoisegenerator != null) {
                d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
            }
            ImprovedNoiseGenerator improvednoisegenerator1 = maxNoise.getOctave(i);
            if (improvednoisegenerator1 != null) {
                d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
            }
            if (i < 8) {
                ImprovedNoiseGenerator improvednoisegenerator2 = mainNoise.getOctave(i);
                if (improvednoisegenerator2 != null) {
                    d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double) p_222552_2_ * p_222552_10_ * d3) / d3;
                }
            }
            d3 /= 2;
        }
        return MathHelper.clampedLerp(d0 / 512, d1 / 512, (d2 / 10 + 1) / 2);
    }

    private double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
        double[] adouble = new double[noiseSizeY + 1];
        fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
        return adouble;
    }

    private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
        NoiseSettings noisesettings = settings.get().getNoise();
        double d0;
        double d1;
        if (islandNoise != null) {
            d0 = EndBiomeProvider.getRandomNoise(islandNoise, noiseX, noiseZ) - 8;
            if (d0 > 0) {
                d1 = 0.25D;
            } else {
                d1 = 1;
            }
        } else {
            float f = 0;
            float f1 = 0;
            float f2 = 0;
            int j = getSeaLevel();
            float f3 = biomeProvider.getNoiseBiome(noiseX, j, noiseZ).getDepth();
            for (int k = -2; k <= 2; ++k) {
                for (int l = -2; l <= 2; ++l) {
                    Biome biome = biomeProvider.getNoiseBiome(noiseX + k, j, noiseZ + l);
                    float f4 = biome.getDepth();
                    float f5 = biome.getScale();
                    float f6;
                    float f7;
                    if (noisesettings.func_236181_l_() && f4 > 0) {
                        f6 = 1 + f4 * 2;
                        f7 = 1 + f5 * 4;
                    } else {
                        f6 = f4;
                        f7 = f5;
                    }

                    float f8 = f4 > f3 ? 0.5F : 1;
                    float f9 = f8 * BIOME_WEIGHTS[k + 2 + (l + 2) * 5] / (f6 + 2);
                    f += f7 * f9;
                    f1 += f6 * f9;
                    f2 += f9;
                }
            }
            float f10 = f1 / f2;
            float f11 = f / f2;
            double d16 = f10 * 0.5F - 0.125F;
            double d18 = f11 * 0.9F + 0.1F;
            d0 = d16 * 0.265625D;
            d1 = 96 / d18;
        }
        double d12 = 684.412D * noisesettings.func_236171_b_().func_236151_a_();
        double d13 = 684.412D * noisesettings.func_236171_b_().func_236153_b_();
        double d14 = d12 / noisesettings.func_236171_b_().func_236154_c_();
        double d15 = d13 / noisesettings.func_236171_b_().func_236155_d_();
        double d17 = noisesettings.func_236172_c_().func_236186_a_();
        double d19 = noisesettings.func_236172_c_().func_236188_b_();
        double d20 = noisesettings.func_236172_c_().func_236189_c_();
        double d21 = noisesettings.func_236173_d_().func_236186_a_();
        double d2 = noisesettings.func_236173_d_().func_236188_b_();
        double d3 = noisesettings.func_236173_d_().func_236189_c_();
        double d4 = noisesettings.func_236179_j_() ? func_236095_c_(noiseX, noiseZ) : 0;
        double d5 = noisesettings.func_236176_g_();
        double d6 = noisesettings.func_236177_h_();
        for (int i1 = 0; i1 <= noiseSizeY; ++i1) {
            double d7 = func_222552_a(noiseX, i1, noiseZ, d12, d13, d14, d15);
            double d8 = 1 - (double) i1 * 2 / (double) noiseSizeY + d4;
            double d9 = d8 * d5 + d6;
            double d10 = (d9 + d0) * d1;
            if (d10 > 0) {
                d7 = d7 + d10 * 4;
            } else {
                d7 = d7 + d10;
            }
            if (d19 > 0) {
                double d11 = ((double) (noiseSizeY - i1) - d20) / d19;
                d7 = MathHelper.clampedLerp(d17, d7, d11);
            }
            if (d2 > 0) {
                double d22 = ((double) i1 - d3) / d2;
                d7 = MathHelper.clampedLerp(d21, d7, d22);
            }
            noiseColumn[i1] = d7;
        }
    }

    private double func_236095_c_(int p_236095_1_, int p_236095_2_) {
        double d0 = depthNoise.getValue(p_236095_1_ * 200, 10, p_236095_2_ * 200, 1, 0, true);
        double d1;
        if (d0 < 0) {
            d1 = -d0 * 0.3D;
        } else {
            d1 = d0;
        }
        double d2 = d1 * 24.575625D - 2;
        return d2 < 0 ? d2 * 0.009486607142857142D : Math.min(d2, 1) * 0.006640625D;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return func_236087_a_(x, z, null, heightmapType.getHeightLimitPredicate());
    }

    @Override
    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
        BlockState[] ablockstate = new BlockState[noiseSizeY * verticalNoiseGranularity];
        func_236087_a_(p_230348_1_, p_230348_2_, ablockstate, null);
        return new Blockreader(ablockstate);
    }

    private int func_236087_a_(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_) {
        int i = Math.floorDiv(p_236087_1_, horizontalNoiseGranularity);
        int j = Math.floorDiv(p_236087_2_, horizontalNoiseGranularity);
        int k = Math.floorMod(p_236087_1_, horizontalNoiseGranularity);
        int l = Math.floorMod(p_236087_2_, horizontalNoiseGranularity);
        double d0 = (double) k / (double) horizontalNoiseGranularity;
        double d1 = (double) l / (double) horizontalNoiseGranularity;
        double[][] adouble = new double[][]{func_222547_b(i, j), func_222547_b(i, j + 1), func_222547_b(i + 1, j), func_222547_b(i + 1, j + 1)};
        BiomeProvider provider = getBiomeProvider();
        for (int i1 = noiseSizeY - 1; i1 >= 0; --i1) {
            double d2 = adouble[0][i1];
            double d3 = adouble[1][i1];
            double d4 = adouble[2][i1];
            double d5 = adouble[3][i1];
            double d6 = adouble[0][i1 + 1];
            double d7 = adouble[1][i1 + 1];
            double d8 = adouble[2][i1 + 1];
            double d9 = adouble[3][i1 + 1];
            for (int j1 = verticalNoiseGranularity - 1; j1 >= 0; --j1) {
                double d10 = (double) j1 / (double) verticalNoiseGranularity;
                double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int k1 = i1 * verticalNoiseGranularity + j1;
                BlockState blockstate = func_236086_a_(provider.getNoiseBiome(p_236087_1_, k1, p_236087_2_), d11, k1);
                if (p_236087_3_ != null) {
                    p_236087_3_[k1] = blockstate;
                }
                if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
                    return k1 + 1;
                }
            }
        }
        return 0;
    }

    private BlockState func_236086_a_(Biome biome, double p_236086_1_, int y) {
        Pair<BlockState, BlockState> states = null;
        if (defaults.containsKey(biome)) {
            states = defaults.get(biome);
        } else {
            for (Dimension dim : ServerLifecycleHooks.getCurrentServer().getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_()) {
                ChunkGenerator gen = dim.getChunkGenerator();
                if (gen instanceof NoiseChunkGenerator && gen.getBiomeProvider().getBiomes().contains(biome)) {
                    DimensionSettings settings = SETTINGS.apply((NoiseChunkGenerator) gen).get();
                    states = Pair.of(settings.getDefaultBlock(), settings.getDefaultFluid());
                    break;
                }
            }
            if (states == null) {
                states = Pair.of(Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState());
            }
            defaults.put(biome, states);
        }
        if (p_236086_1_ > 0) {
            return states.getFirst();
        } else if (y < getSeaLevel()) {
            return states.getSecond();
        } else {
            return AIR;
        }
    }

    @Override
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
        ChunkPos chunkpos = p_225551_2_.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(i, j);
        ChunkPos chunkpos1 = p_225551_2_.getPos();
        int k = chunkpos1.getXStart();
        int l = chunkpos1.getZStart();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int i1 = 0; i1 < 16; ++i1) {
            for (int j1 = 0; j1 < 16; ++j1) {
                int k1 = k + i1;
                int l1 = l + j1;
                int i2 = p_225551_2_.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
                double d1 = surfaceNoise.noiseAt((double) k1 * 0.0625D, (double) l1 * 0.0625D, 0.0625D, (double) i1 * 0.0625D) * 15;
                p_225551_1_.getBiome(blockpos$mutable.setPos(k + i1, i2, l + j1)).buildSurface(sharedseedrandom, p_225551_2_, k1, l1, i2, d1, defaultBlock, defaultFluid, getSeaLevel(), p_225551_1_.getSeed());
            }
        }
        makeBedrock(p_225551_2_, sharedseedrandom);
    }

    private void makeBedrock(IChunk chunkIn, Random rand) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = chunkIn.getPos().getXStart();
        int j = chunkIn.getPos().getZStart();
        DimensionSettings dimensionsettings = settings.get();
        int k = dimensionsettings.getBedrockFloorPosition();
        int l = max - 1 - dimensionsettings.getBedrockRoofPosition();
        boolean flag = l + 4 >= 0 && l < max;
        boolean flag1 = k + 4 >= 0 && k < max;
        if (flag || flag1) {
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15)) {
                if (flag) {
                    for (int j1 = 0; j1 < 5; ++j1) {
                        if (j1 <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), l - j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                        }
                    }
                }
                if (flag1) {
                    for (int k1 = 4; k1 >= 0; --k1) {
                        if (k1 <= rand.nextInt(5)) {
                            chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), k + k1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
        ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        ChunkPos chunkpos = p_230352_3_.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        int k = i << 4;
        int l = j << 4;
        for (Structure<?> structure : Structure.field_236384_t_) {
            p_230352_2_.func_235011_a_(SectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
                for (StructurePiece structurepiece1 : p_236089_5_.getComponents()) {
                    if (structurepiece1.func_214810_a(chunkpos, 12)) {
                        if (structurepiece1 instanceof AbstractVillagePiece) {
                            AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) structurepiece1;
                            JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
                            if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }
                            for (JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                                int l5 = jigsawjunction1.getSourceX();
                                int i6 = jigsawjunction1.getSourceZ();
                                if (l5 > k - 12 && i6 > l - 12 && l5 < k + 15 + 12 && i6 < l + 15 + 12) {
                                    objectlist1.add(jigsawjunction1);
                                }
                            }
                        } else {
                            objectlist.add(structurepiece1);
                        }
                    }
                }
            });
        }
        double[][][] adouble = new double[2][noiseSizeZ + 1][noiseSizeY + 1];
        for (int i5 = 0; i5 < noiseSizeZ + 1; ++i5) {
            adouble[0][i5] = new double[noiseSizeY + 1];
            fillNoiseColumn(adouble[0][i5], i * noiseSizeX, j * noiseSizeZ + i5);
            adouble[1][i5] = new double[noiseSizeY + 1];
        }
        Biome biome = getBiomeProvider().getNoiseBiome(k, 0, l);
        ChunkPrimer chunkprimer = (ChunkPrimer) p_230352_3_;
        Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> objectlistiterator = objectlist.iterator();
        ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();
        for (int i1 = 0; i1 < noiseSizeX; ++i1) {
            for (int j1 = 0; j1 < noiseSizeZ + 1; ++j1) {
                fillNoiseColumn(adouble[1][j1], i * noiseSizeX + i1 + 1, j * noiseSizeZ + j1);
            }
            for (int j5 = 0; j5 < noiseSizeZ; ++j5) {
                ChunkSection chunksection = chunkprimer.getSection(15);
                chunksection.lock();
                for (int k1 = noiseSizeY - 1; k1 >= 0; --k1) {
                    double d0 = adouble[0][j5][k1];
                    double d1 = adouble[0][j5 + 1][k1];
                    double d2 = adouble[1][j5][k1];
                    double d3 = adouble[1][j5 + 1][k1];
                    double d4 = adouble[0][j5][k1 + 1];
                    double d5 = adouble[0][j5 + 1][k1 + 1];
                    double d6 = adouble[1][j5][k1 + 1];
                    double d7 = adouble[1][j5 + 1][k1 + 1];
                    for (int l1 = verticalNoiseGranularity - 1; l1 >= 0; --l1) {
                        int i2 = k1 * verticalNoiseGranularity + l1;
                        int j2 = i2 & 15;
                        int k2 = i2 >> 4;
                        if (chunksection.getYLocation() >> 4 != k2) {
                            chunksection.unlock();
                            chunksection = chunkprimer.getSection(k2);
                            chunksection.lock();
                        }
                        double d8 = (double) l1 / (double) verticalNoiseGranularity;
                        double d9 = MathHelper.lerp(d8, d0, d4);
                        double d10 = MathHelper.lerp(d8, d2, d6);
                        double d11 = MathHelper.lerp(d8, d1, d5);
                        double d12 = MathHelper.lerp(d8, d3, d7);
                        for (int l2 = 0; l2 < horizontalNoiseGranularity; ++l2) {
                            int i3 = k + i1 * horizontalNoiseGranularity + l2;
                            int j3 = i3 & 15;
                            double d13 = (double) l2 / (double) horizontalNoiseGranularity;
                            double d14 = MathHelper.lerp(d13, d9, d10);
                            double d15 = MathHelper.lerp(d13, d11, d12);
                            for (int k3 = 0; k3 < horizontalNoiseGranularity; ++k3) {
                                int l3 = l + j5 * horizontalNoiseGranularity + k3;
                                int i4 = l3 & 15;
                                double d16 = (double) k3 / (double) horizontalNoiseGranularity;
                                double d17 = MathHelper.lerp(d16, d14, d15);
                                double d18 = MathHelper.clamp(d17 / 200, -1, 1);
                                int j4;
                                int k4;
                                int l4;
                                for (d18 = d18 / 2 - d18 * d18 * d18 / 24; objectlistiterator.hasNext(); d18 += func_222556_a(j4, k4, l4) * 0.8D) {
                                    StructurePiece structurepiece = objectlistiterator.next();
                                    MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
                                    j4 = Math.max(0, Math.max(mutableboundingbox.minX - i3, i3 - mutableboundingbox.maxX));
                                    k4 = i2 - (mutableboundingbox.minY + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) structurepiece).getGroundLevelDelta() : 0));
                                    l4 = Math.max(0, Math.max(mutableboundingbox.minZ - l3, l3 - mutableboundingbox.maxZ));
                                }
                                objectlistiterator.back(objectlist.size());
                                while (objectlistiterator1.hasNext()) {
                                    JigsawJunction jigsawjunction = objectlistiterator1.next();
                                    int k5 = i3 - jigsawjunction.getSourceX();
                                    j4 = i2 - jigsawjunction.getSourceGroundY();
                                    k4 = l3 - jigsawjunction.getSourceZ();
                                    d18 += func_222556_a(k5, j4, k4) * 0.4D;
                                }
                                objectlistiterator1.back(objectlist1.size());
                                BlockState blockstate = func_236086_a_(biome, d18, i2);
                                if (blockstate != AIR) {
                                    blockpos$mutable.setPos(i3, i2, l3);
                                    if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
                                        chunkprimer.addLightPosition(blockpos$mutable);
                                    }
                                    chunksection.setBlockState(j3, j2, i4, blockstate, false);
                                    heightmap.update(j3, i2, i4, blockstate);
                                    heightmap1.update(j3, i2, i4, blockstate);
                                }
                            }
                        }
                    }
                }
                chunksection.unlock();
            }
            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }
    }

    @Override
    public int getMaxBuildHeight() {
        return max;
    }

    @Override
    public int getSeaLevel() {
        return settings.get().getSeaLevel();
    }

    @Override
    public List<MobSpawnInfo.Spawners> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_) {
        List<MobSpawnInfo.Spawners> spawns = net.minecraftforge.common.world.StructureSpawnManager.getStructureSpawns(p_230353_2_, p_230353_3_, p_230353_4_);
        if (spawns != null) {
            return spawns;
        }
        return super.func_230353_a_(p_230353_1_, p_230353_2_, p_230353_3_, p_230353_4_);
    }

    @Override
    public void func_230354_a_(WorldGenRegion p_230354_1_) {
        if (!MOB_GENERATION_DISABLED.apply(settings.get())) {
            int i = p_230354_1_.getMainChunkX();
            int j = p_230354_1_.getMainChunkZ();
            Biome biome = p_230354_1_.getBiome((new ChunkPos(i, j)).asBlockPos());
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
            sharedseedrandom.setDecorationSeed(p_230354_1_.getSeed(), i << 4, j << 4);
            WorldEntitySpawner.performWorldGenSpawning(p_230354_1_, biome, i, j, sharedseedrandom);
        }
    }
}

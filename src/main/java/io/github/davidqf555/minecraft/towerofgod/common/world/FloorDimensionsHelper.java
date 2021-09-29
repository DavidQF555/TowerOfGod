package io.github.davidqf555.minecraft.towerofgod.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientDimensionsPacket;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class FloorDimensionsHelper {

    private static final double TYPE_RATE = 0.1;

    public static void forceSendPlayerToFloor(ServerPlayerEntity player, int floor, Vector3d pos) {
        ServerWorld world = getOrCreateWorld(player.server, floor);
        world.getChunk(SectionPos.toChunk((int) pos.getX()), SectionPos.toChunk((int) pos.getZ()));
        player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
    }

    public static void sendPlayerToFloor(ServerPlayerEntity serverPlayer, BlockPos teleporter, Direction direction, int floor) {
        ServerWorld world = getOrCreateWorld(serverPlayer.server, floor);
        if (serverPlayer.canChangeDimension()) {
            serverPlayer.changeDimension(world, new FloorTeleporter(world, teleporter, direction));
        }
    }

    private static RegistryKey<World> createWorldKey(int level) {
        return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(TowerOfGod.MOD_ID, "floor_" + level));
    }

    @Nullable
    public static FloorProperty getFloorProperty(ServerWorld world) {
        return FloorProperty.get(world);
    }

    @SuppressWarnings("deprecation")
    public static ServerWorld getOrCreateWorld(MinecraftServer server, int level) {
        if (level <= 1) {
            return server.getWorld(World.OVERWORLD);
        }
        Map<RegistryKey<World>, ServerWorld> map = server.forgeGetWorldMap();
        RegistryKey<World> worldKey = createWorldKey(level);
        if (map.containsKey(worldKey)) {
            return map.get(worldKey);
        } else {
            return createAndRegisterWorldAndDimension(server, map, worldKey, level);
        }
    }

    @SuppressWarnings("deprecation")
    private static ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server, Map<RegistryKey<World>, ServerWorld> map, RegistryKey<World> worldKey, int level) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        RegistryKey<Dimension> dimensionKey = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, worldKey.getLocation());
        FloorProperty property = randomProperty(level, overworld.getRandom());
        Dimension dimension = createFloorDimension(server, level, property);
        IServerConfiguration serverConfig = server.getServerConfiguration();
        DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.getDimensionGeneratorSettings();
        dimensionGeneratorSettings.func_236224_e_().register(dimensionKey, dimension, Lifecycle.experimental());
        DerivedWorldInfo derivedWorldInfo = new DerivedWorldInfo(serverConfig, serverConfig.getServerWorldInfo());
        ServerWorld newWorld = new ServerWorld(server, server.backgroundExecutor, server.anvilConverterForAnvilFile, derivedWorldInfo, worldKey, dimension.getDimensionType(), server.chunkStatusListenerFactory.create(11), dimension.getChunkGenerator(), dimensionGeneratorSettings.hasDebugChunkGenerator(), BiomeManager.getHashedSeed(dimensionGeneratorSettings.getSeed()), ImmutableList.of(), false);
        overworld.getWorldBorder().addListener(new IBorderListener.Impl(newWorld.getWorldBorder()));
        FloorProperty.set(newWorld, property);
        map.put(worldKey, newWorld);
        server.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));
        TowerOfGod.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateClientDimensionsPacket(worldKey));
        return newWorld;
    }

    private static Dimension createFloorDimension(MinecraftServer server, int level, FloorProperty property) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        long seed = overworld.getSeed() + level - 1;
        SharedSeedRandom random = new SharedSeedRandom(seed);
        float lighting = property.hasCeiling() ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        ResourceLocation effect;
        int rand = random.nextInt(3);
        if (rand == 0) {
            effect = DimensionType.OVERWORLD_ID;
        } else if (rand == 1) {
            effect = DimensionType.THE_NETHER_ID;
        } else {
            effect = DimensionType.THE_END_ID;
        }
        BiomeProvider provider = new NetherBiomeProvider(seed, property.getBiomeAttributesList(server.getDynamicRegistries().getRegistry(Registry.BIOME_KEY)), Optional.empty());
        ChunkGenerator generator = new FloorChunkGenerator(provider, seed, () -> createSettings(property));
        return new Dimension(() -> new FloorDimensionType(property, 1, effect, lighting), generator);
    }

    private static DimensionSettings createSettings(FloorProperty property) {
        boolean hasFloor = property.hasFloor();
        boolean hasCeiling = property.hasCeiling();
        int height = hasCeiling || !hasFloor ? 128 : 256;
        int sizeHorizontal;
        int sizeVertical;
        int topTarget;
        int topSize;
        int topOffset;
        int bottomTarget;
        int bottomSize;
        int bottomOffset;
        double densityFactor;
        double densityOffset;
        double xzScale;
        double yScale;
        double xzFactor = 80;
        double yFactor;
        int seaLevel;
        if (hasFloor) {
            sizeHorizontal = 1;
            sizeVertical = 2;
            if (hasCeiling) {
                topTarget = 120;
                topSize = 3;
                topOffset = 0;
                bottomTarget = 320;
                bottomSize = 4;
                bottomOffset = -1;
                densityFactor = 0;
                densityOffset = 0.02;
                yFactor = 60;
                seaLevel = 32;
                xzScale = 1;
                yScale = 3;
            } else {
                topTarget = -10;
                topSize = 3;
                topOffset = 0;
                bottomTarget = -30;
                bottomSize = 0;
                bottomOffset = 0;
                densityFactor = 1;
                densityOffset = -0.5;
                yFactor = 160;
                seaLevel = 63;
                xzScale = 1;
                yScale = 1;
            }
        } else {
            if (hasCeiling) {
                sizeHorizontal = 1;
                sizeVertical = 2;
                topTarget = 120;
                topSize = 3;
                topOffset = 0;
                bottomTarget = -30;
                bottomSize = 7;
                bottomOffset = 1;
                densityFactor = 0;
                densityOffset = 0;
                yFactor = 30;
                xzScale = 1;
            } else {
                sizeHorizontal = 2;
                sizeVertical = 1;
                topTarget = -3000;
                topSize = 64;
                topOffset = -46;
                bottomTarget = -30;
                bottomSize = 7;
                bottomOffset = 1;
                densityFactor = 0;
                densityOffset = 0;
                yFactor = 160;
                xzScale = 2;
            }
            yScale = 1;
            seaLevel = 0;
        }
        int ceilingOffset = hasCeiling ? 0 : -10;
        int floorOffset = hasFloor ? 0 : -10;
        SlideSettings topSlide = new SlideSettings(topTarget, topSize, topOffset);
        SlideSettings bottomSlide = new SlideSettings(bottomTarget, bottomSize, bottomOffset);
        ScalingSettings sampling = new ScalingSettings(xzScale, yScale, xzFactor, yFactor);
        NoiseSettings noise = new NoiseSettings(height, sampling, topSlide, bottomSlide, sizeHorizontal, sizeVertical, densityFactor, densityOffset, false, true, false, false);
        Map<Structure<?>, StructureSeparationSettings> map = Maps.newHashMap(DimensionStructuresSettings.field_236191_b_);
        if (hasCeiling) {
            for (Structure<?> structure : new HashSet<>(map.keySet())) {
                if (structure.getDecorationStage() == GenerationStage.Decoration.SURFACE_STRUCTURES) {
                    map.remove(structure);
                }
            }
        }
        DimensionStructuresSettings structures = new DimensionStructuresSettings(Optional.empty(), map);
        return new DimensionSettings(structures, noise, Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), ceilingOffset, floorOffset, seaLevel, false);
    }

    private static FloorProperty randomProperty(int level, Random rand) {
        Set<BiomeDictionary.Type> types = new HashSet<>();
        List<BiomeDictionary.Type> all = new ArrayList<>(BiomeDictionary.Type.getAll());
        for (BiomeDictionary.Type type : all) {
            if (rand.nextDouble() < TYPE_RATE) {
                types.add(type);
            }
        }
        if (types.isEmpty()) {
            types.add(all.get(rand.nextInt(all.size())));
        }
        EnumSet<FloorProperty.Bound> bounds = EnumSet.noneOf(FloorProperty.Bound.class);
        for (FloorProperty.Bound bound : FloorProperty.Bound.values()) {
            if (rand.nextBoolean()) {
                bounds.add(bound);
            }
        }
        FloorProperty.Time time;
        if (bounds.contains(FloorProperty.Bound.CEILING)) {
            time = FloorProperty.Time.MIDNIGHT;
        } else if (rand.nextBoolean()) {
            time = FloorProperty.Time.DYNAMIC;
        } else {
            FloorProperty.Time[] times = FloorProperty.Time.values();
            time = times[rand.nextInt(times.length)];
        }
        float density = 0.9f + level * 0.1f + rand.nextFloat() * 0.5f - 0.25f;
        return new FloorProperty(level, types, bounds, time, density);
    }
}

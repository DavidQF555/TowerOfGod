package io.github.davidqf555.minecraft.towerofgod.common;

import io.github.davidqf555.minecraft.towerofgod.common.commands.FloorCommand;
import io.github.davidqf555.minecraft.towerofgod.common.commands.ShinsuCommand;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.gen.DataGenItemModelProvider;
import io.github.davidqf555.minecraft.towerofgod.common.data.gen.DataGenRecipeProvider;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RankerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.*;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorChunkGenerator;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumMap;
import java.util.Map;

public final class EventBusSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ConfiguredFeature<?, ?> SUSPENDIUM_ORE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8)).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(17, 0, 100))).square().count(3);
    private static int index = 0;

    private EventBusSubscriber() {
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            Entity entity = event.getObject();
            if (entity instanceof IShinsuUser || entity instanceof PlayerEntity) {
                event.addCapability(SHINSU_STATS, new ShinsuStats.Provider());
            }
        }

        @SubscribeEvent
        public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Entity entity = event.getEntity();
            ShinsuStats stats = ShinsuStats.get(entity);
            Map<ShinsuTechniqueType, ShinsuTechniqueData> data = new EnumMap<>(ShinsuTechniqueType.class);
            for (ShinsuTechniqueType type : ShinsuTechniqueType.values()) {
                data.put(type, stats.getData(type));
            }
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientShinsuDataPacket(data));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
        }

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            ShinsuCommand.register(event.getDispatcher());
            FloorCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> SUSPENDIUM_ORE);
            if (event.getCategory() != Biome.Category.OCEAN && event.getCategory() != Biome.Category.RIVER) {
                MobSpawnInfoBuilder builder = event.getSpawns();
                builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(RegistryHandler.REGULAR_ENTITY.get(), 3, 1, 1));
                builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(RegistryHandler.RANKER_ENTITY.get(), 1, 1, 1));
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player.world.getGameTime() % ServerConfigs.INSTANCE.shinsuUpdatePeriod.get() == 0) {
                ShinsuStats.get(event.player).periodicTick((ServerWorld) event.player.world, ServerConfigs.INSTANCE.shinsuUpdatePeriod.get());
            }
        }

        @SubscribeEvent
        public static void onClonePlayerEvent(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                ServerPlayerEntity original = (ServerPlayerEntity) event.getOriginal();
                ServerPlayerEntity resp = (ServerPlayerEntity) event.getPlayer();
                ShinsuStats.get(resp).deserializeNBT(ShinsuStats.get(original).serializeNBT());
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (entity instanceof IShinsuUser) {
                Entity source = event.getSource().getTrueSource();
                if (source instanceof IShinsuUser || source instanceof PlayerEntity) {
                    ShinsuStats.get(source).onKill(source, ShinsuStats.get(entity));
                }
            }
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.world instanceof ServerWorld && event.phase == TickEvent.Phase.START) {
                if (event.world.getGameTime() % 100 == 0) {
                    RegularTeamsSavedData.getOrCreate((ServerWorld) event.world).update((ServerWorld) event.world);
                }
                int period = ServerConfigs.INSTANCE.shinsuUpdatePeriod.get();
                if (event.world.getGameTime() % period == 0) {
                    ((ServerWorld) event.world).getEntities()
                            .filter(entity -> entity instanceof IShinsuUser)
                            .forEach(entity -> ShinsuStats.get(entity).periodicTick((ServerWorld) event.world, period));
                }
            }
        }

        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            EffectInstance reinforcement = entity.getActivePotionEffect(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get());
            if (reinforcement != null) {
                Vector3d motion = entity.getMotion().add(0, reinforcement.getAmplifier() * 0.025 + 0.025, 0);
                entity.setMotion(motion);
            }
            EffectInstance reverse = entity.getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get());
            if (reverse != null) {
                Vector3d motion = entity.getMotion();
                entity.setMotion(motion.getX(), Math.max(0, motion.getY() - reverse.getAmplifier() * 0.025 - 0.025), motion.getZ());
            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            LivingEntity entity = event.getEntityLiving();
            EffectInstance effect = entity.getActivePotionEffect(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get());
            if (effect != null) {
                event.setDistance(event.getDistance() - effect.getAmplifier() * 0.5f - 0.5f);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onGatherDataEvent(GatherDataEvent event) {
            DataGenerator gen = event.getGenerator();
            if (event.includeClient()) {
                gen.addProvider(new DataGenItemModelProvider(gen, event.getExistingFileHelper()));
            }
            if (event.includeServer()) {
                gen.addProvider(new DataGenRecipeProvider(gen));
            }
        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseEntity.setAttributes().create());
            event.put(RegistryHandler.OBSERVER_ENTITY.get(), ObserverEntity.setAttributes().create());
            event.put(RegistryHandler.REGULAR_ENTITY.get(), RegularEntity.setAttributes().create());
            event.put(RegistryHandler.RANKER_ENTITY.get(), RankerEntity.setAttributes().create());
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            CapabilityManager.INSTANCE.register(ShinsuStats.class, new ShinsuStats.Storage(), ShinsuStats::new);
            event.enqueueWork(() -> {
                CastShinsuPacket.register(index++);
                UpdateShinsuMeterPacket.register(index++);
                UpdateBaangsMeterPacket.register(index++);
                UpdateClientErrorPacket.register(index++);
                UpdateClientShinsuDataPacket.register(index++);
                ObserverChangeHighlightPacket.register(index++);
                UpdateClientDimensionsPacket.register(index++);
                OpenFloorTeleportationTerminalPacket.register(index++);
                ChangeFloorPacket.register(index++);
                OpenGuideScreenPacket.register(index++);
                Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), SUSPENDIUM_ORE);
                Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(TowerOfGod.MOD_ID, "floor_chunk_generator_codec"), FloorChunkGenerator.CODEC);
                EntitySpawnPlacementRegistry.register(RegistryHandler.REGULAR_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesWithinAABB(RegularEntity.class, new AxisAlignedBB(pos).grow(64)).size() < 10);
                EntitySpawnPlacementRegistry.register(RegistryHandler.RANKER_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesWithinAABB(RankerEntity.class, new AxisAlignedBB(pos).grow(64)).size() < 1);
            });
        }
    }
}
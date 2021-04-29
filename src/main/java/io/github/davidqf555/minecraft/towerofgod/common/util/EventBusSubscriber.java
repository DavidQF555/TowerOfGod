package io.github.davidqf555.minecraft.towerofgod.common.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.items.ShinsuItemColor;
import io.github.davidqf555.minecraft.towerofgod.common.packets.*;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorBiomeProvider;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
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
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class EventBusSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PLAYER_EQUIPS = new ResourceLocation(TowerOfGod.MOD_ID, "player_equips");
    private static final ConfiguredFeature<?, ?> SUSPENDIUM_ORE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8)).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(17, 0, 100))).square().count(3);
    private static IShinsuStats clonedStats = null;
    private static IPlayerShinsuEquips clonedEquips = null;
    private static int index = 0;

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {

        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
            Entity entity = event.getObject();
            if (entity instanceof IShinsuUser || entity instanceof PlayerEntity) {
                event.addCapability(SHINSU_STATS, new IShinsuStats.Provider());
            }
            if (entity instanceof PlayerEntity) {
                event.addCapability(PLAYER_EQUIPS, new IPlayerShinsuEquips.Provider());
            }
        }

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            StatsCommand.register(event.getDispatcher());
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
            if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
                IShinsuStats.get(event.player).tick((ServerWorld) event.player.world);
            }
        }

        @SubscribeEvent
        public static void onClonePlayerEvent(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                ServerPlayerEntity original = (ServerPlayerEntity) event.getOriginal();
                clonedStats = IShinsuStats.get(original);
                clonedEquips = IPlayerShinsuEquips.get(original);
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            if (player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
                IShinsuStats stats = IShinsuStats.get(player);
                stats.deserializeNBT(clonedStats.serializeNBT());
                IPlayerShinsuEquips equips = IPlayerShinsuEquips.get(player);
                equips.deserializeNBT(clonedEquips.serializeNBT());
            }
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.world instanceof ServerWorld && event.phase == TickEvent.Phase.START) {
                RegularTeamsSavedData.getOrCreate((ServerWorld) event.world).tick((ServerWorld) event.world);
                ((ServerWorld) event.world).getEntities()
                        .filter(entity -> entity instanceof IShinsuUser)
                        .forEach(entity -> IShinsuStats.get(entity).tick((ServerWorld) event.world));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingJumpLast(LivingEvent.LivingJumpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
                entity.setMotion(0, 0, 0);
            }
        }

        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            EffectInstance effect = entity.getActivePotionEffect(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get());
            if (effect != null) {
                Vector3d motion = entity.getMotion().add(0, effect.getAmplifier() * 0.05 + 0.05, 0);
                entity.setMotion(motion);
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
    public static class ModBus {

        @SubscribeEvent
        public static void onHandleColors(ColorHandlerEvent.Item event) {
            ShinsuItemColor color = new ShinsuItemColor();
            for (RegistryObject<? extends Item> item : RegistryHandler.SHINSU_ITEMS) {
                event.getItemColors().register(color, item::get);
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
            CapabilityManager.INSTANCE.register(IShinsuStats.class, new IShinsuStats.Storage(), IShinsuStats.ShinsuStats::new);
            CapabilityManager.INSTANCE.register(IPlayerShinsuEquips.class, new IPlayerShinsuEquips.Storage(), new IPlayerShinsuEquips.PlayerShinsuEquips.Factory());
            ChangeEquipsMessage.register(index++);
            CastShinsuMessage.register(index++);
            UpdateStatsMetersMessage.register(index++);
            UpdateClientCooldownsMessage.register(index++);
            UpdateClientCanCastMessage.register(index++);
            UpdateClientKnownMessage.register(index++);
            UpdateClientEquippedMessage.register(index++);
            ObserverChangeHighlightMessage.register(index++);
            UpdateClientDimensionsMessage.register(index++);
            OpenFloorTeleportationTerminalMessage.register(index++);
            ChangeFloorMessage.register(index++);
            event.enqueueWork(() -> {
                Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), SUSPENDIUM_ORE);
                Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation(TowerOfGod.MOD_ID, "floor_biome_provider_codec"), FloorBiomeProvider.CODEC);
                EntitySpawnPlacementRegistry.register(RegistryHandler.REGULAR_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesWithinAABB(RegularEntity.class, new AxisAlignedBB(pos).grow(64)).size() < 10);
                EntitySpawnPlacementRegistry.register(RegistryHandler.RANKER_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesWithinAABB(RankerEntity.class, new AxisAlignedBB(pos).grow(64)).size() < 1);
            });
        }
    }
}
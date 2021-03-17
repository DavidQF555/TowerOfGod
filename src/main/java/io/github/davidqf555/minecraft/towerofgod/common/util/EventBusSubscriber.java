package io.github.davidqf555.minecraft.towerofgod.common.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ObserverEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuUserEntity;
import io.github.davidqf555.minecraft.towerofgod.common.items.ShinsuItemColor;
import io.github.davidqf555.minecraft.towerofgod.common.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class EventBusSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PLAYER_EQUIPS = new ResourceLocation(TowerOfGod.MOD_ID, "player_equips");
    private static ConfiguredFeature<?, ?> SUSPENDIUM_ORE;
    private static int index = 0;

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {

        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
            Entity entity = event.getObject();
            if (entity instanceof ShinsuUserEntity || entity instanceof PlayerEntity) {
                event.addCapability(SHINSU_STATS, new IShinsuStats.Provider());
            }
            if (entity instanceof PlayerEntity) {
                event.addCapability(PLAYER_EQUIPS, new IPlayerShinsuEquips.Provider());
            }
        }

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            StatsCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            if (event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND) {
                event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> SUSPENDIUM_ORE);
                if (event.getCategory() != Biome.Category.OCEAN && event.getCategory() != Biome.Category.RIVER) {
                    event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(RegistryHandler.REGULAR_ENTITY.get(), 8, 1, 8));
                }
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
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            CapabilityManager.INSTANCE.register(IShinsuStats.class, new IShinsuStats.Storage(), IShinsuStats.ShinsuStats::new);
            CapabilityManager.INSTANCE.register(IPlayerShinsuEquips.class, new IPlayerShinsuEquips.Storage(), new IPlayerShinsuEquips.PlayerShinsuEquips.Factory());
            ChangeEquipsMessage.register(index++);
            CastShinsuMessage.register(index++);
            ShinsuStatsTickMessage.register(index++);
            UpdateStatsMetersMessage.register(index++);
            UpdateClientCooldownsMessage.register(index++);
            UpdateClientCanCastMessage.register(index++);
            UpdateClientKnownMessage.register(index++);
            UpdateClientEquippedMessage.register(index++);
            ObserverChangeHighlightMessage.register(index++);
            RemoveObserverDataMessage.register(index++);
            event.enqueueWork(() -> {
                SUSPENDIUM_ORE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8)).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(17, 0, 100))).square().count(3);
                Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), SUSPENDIUM_ORE);
            });
        }
    }
}
package io.github.davidqf555.minecraft.towerofgod.common.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.items.ShinsuItemColor;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuUserEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class EventBusSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PLAYER_EQUIPS = new ResourceLocation(TowerOfGod.MOD_ID, "player_equips");
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
        public static void onFMLLoadComplete(FMLLoadCompleteEvent event) {
            for (Biome biome : ForgeRegistries.BIOMES) {
                if (biome.getCategory() != Biome.Category.THEEND && biome.getCategory() != Biome.Category.NETHER) {
                    CountRangeConfig range = new CountRangeConfig(3, 17, 0, 100);
                    OreFeatureConfig feature = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8);
                    ConfiguredPlacement<CountRangeConfig> config = Placement.COUNT_RANGE.configure(range);
                    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(feature).withPlacement(config));
                    if (biome.getCategory() != Biome.Category.OCEAN && biome.getCategory() != Biome.Category.RIVER) {
                        biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(RegistryHandler.REGULAR_ENTITY.get(), 8, 1, 8));
                    }
                }
            }
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
        }
    }
}

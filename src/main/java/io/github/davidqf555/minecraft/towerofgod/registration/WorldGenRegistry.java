package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WorldGenRegistry {

    public static ConfiguredFeature<?, ?> SUSPENDIUM_ORE = null;

    private WorldGenRegistry() {
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> SUSPENDIUM_ORE);
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                SUSPENDIUM_ORE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.SUSPENDIUM_ORE.get().defaultBlockState(), 8)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(17, 0, 100))).squared().count(3));
            });
        }
    }
}

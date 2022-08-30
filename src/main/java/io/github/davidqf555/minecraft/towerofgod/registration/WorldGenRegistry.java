package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WorldGenRegistry {

    public static Holder<PlacedFeature> SUSPENDIUM_ORE = null;

    private WorldGenRegistry() {
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(SUSPENDIUM_ORE);
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                Holder<ConfiguredFeature<OreConfiguration, ?>> configured = FeatureUtils.register(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore").toString(), Feature.ORE, new OreConfiguration(OreFeatures.NATURAL_STONE, BlockRegistry.SUSPENDIUM_ORE.get().defaultBlockState(), 8));
                SUSPENDIUM_ORE = PlacementUtils.register(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore").toString(), configured, HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(17), VerticalAnchor.absolute(117))), InSquarePlacement.spread(), CountPlacement.of(3));
            });
        }
    }
}

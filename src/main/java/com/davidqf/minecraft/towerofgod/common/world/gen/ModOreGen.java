package com.davidqf.minecraft.towerofgod.common.world.gen;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModOreGen {

	@SubscribeEvent
	public static void generateOres(FMLLoadCompleteEvent event) {
		for(Biome biome : ForgeRegistries.BIOMES) {
			if(biome.getCategory() != Biome.Category.THEEND && biome.getCategory() != Biome.Category.NETHER) {
				CountRangeConfig range = new CountRangeConfig(3, 17, 0, 100);
				OreFeatureConfig feature = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8);
				ConfiguredPlacement<CountRangeConfig> config = Placement.COUNT_RANGE.configure(range);
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(feature).withPlacement(config));
			}
		}
	}
}

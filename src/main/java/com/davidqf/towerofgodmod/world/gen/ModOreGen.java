package com.davidqf.towerofgodmod.world.gen;

import com.davidqf.towerofgodmod.*;
import com.davidqf.towerofgodmod.util.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.*;
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
				genOre(biome, 10, 17, 0, 100, OreFeatureConfig.FillerBlockType.NATURAL_STONE, RegistryHandler.SUSPENDIUM_ORE.get().getDefaultState(), 8);
			}
		}
	}

	private static void genOre(Biome biome, int count, int bottom, int top, int max, OreFeatureConfig.FillerBlockType filler, BlockState defaultState, int size) {
		CountRangeConfig range = new CountRangeConfig(count, bottom, top, max);
		OreFeatureConfig feature = new OreFeatureConfig(filler, defaultState, size);
		ConfiguredPlacement<CountRangeConfig> config = Placement.COUNT_RANGE.configure(range);
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(feature).withPlacement(config));
	}

}

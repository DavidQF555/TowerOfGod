package com.davidqf.minecraft.towerofgod.common.world.gen;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntitySpawnGen {

    @SubscribeEvent
    public static void spawnEntities(FMLLoadCompleteEvent event) {
//        for (Biome biome : ForgeRegistries.BIOMES) {
//            if (biome.getCategory() != Biome.Category.THEEND && biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.OCEAN && biome.getCategory() != Biome.Category.RIVER) {
//                biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(RegistryHandler.REGULAR_ENTITY.get(), 8, 1, 8));
//            }
//        }
    }
}

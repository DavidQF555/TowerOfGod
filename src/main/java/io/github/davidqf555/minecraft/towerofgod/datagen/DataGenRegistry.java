package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
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
}

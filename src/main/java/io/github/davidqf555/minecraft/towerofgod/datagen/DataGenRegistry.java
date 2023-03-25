package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherDataEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        if (event.includeClient()) {
            gen.addProvider(true, new DataGenItemModelProvider(output, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            gen.addProvider(true, new DataGenRecipeProvider(output));
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CapabilityRegistry {

    private CapabilityRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ShinsuStats.class, new NBTCapabilityStorage<>(), ShinsuStats::new);
    }

}

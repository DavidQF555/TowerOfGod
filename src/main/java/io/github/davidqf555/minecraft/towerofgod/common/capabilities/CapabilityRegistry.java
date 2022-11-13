package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.CastingData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
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
        CapabilityManager.INSTANCE.register(ShinsuQualityData.class, new NBTCapabilityStorage<>(), ShinsuQualityData::new);
        CapabilityManager.INSTANCE.register(ShinsuTechniqueData.class, new NBTCapabilityStorage<>(), ShinsuTechniqueData::new);
        CapabilityManager.INSTANCE.register(PredictedShinsuQuality.class, new NBTCapabilityStorage<>(), PredictedShinsuQuality::new);
        CapabilityManager.INSTANCE.register(CastingData.class, new NBTCapabilityStorage<>(), CastingData::new);
    }

}

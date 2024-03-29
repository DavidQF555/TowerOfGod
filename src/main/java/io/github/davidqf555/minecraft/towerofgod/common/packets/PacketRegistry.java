package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PacketRegistry {

    private static int index = 0;

    private PacketRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CastShinsuPacket.register(index++);
            UpdateShinsuMeterPacket.register(index++);
            ClientUpdateClientErrorPacket.register(index++);
            ServerUpdateClientErrorPacket.register(index++);
            ObserverChangeHighlightPacket.register(index++);
            ServerOpenCombinationGUIPacket.register(index++);
            OpenGuideScreenPacket.register(index++);
            ClientUpdateCastingPacket.register(index++);
            ServerUpdateCastingPacket.register(index++);
            ServerUpdateAttributePacket.register(index++);
            ServerUpdateUnlockedPacket.register(index++);
            ClientOpenCombinationGUIPacket.register(index++);
        });
    }
}

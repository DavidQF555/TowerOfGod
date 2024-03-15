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
            ClientUpdateBaangsPacket.register(index++);
            ObserverChangeHighlightPacket.register(index++);
            OpenGuideScreenPacket.register(index++);
            ServerUpdateAttributePacket.register(index++);
            ServerUpdateBaangsPacket.register(index++);
            ServerUpdateUnlockedPacket.register(index++);
            UpdateCastingPacket.register(index++);
        });
    }
}

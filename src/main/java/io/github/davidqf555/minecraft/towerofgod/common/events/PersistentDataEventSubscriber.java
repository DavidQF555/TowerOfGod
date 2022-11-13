package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateCastingPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PersistentDataEventSubscriber {

    private PersistentDataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        if (player instanceof ServerPlayerEntity && target instanceof PlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerUpdateAttributePacket(target.getId(), ShinsuQualityData.get(target).getAttribute()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerUpdateCastingPacket(target.getId(), ClientReference.isCasting((PlayerEntity) target)));
        }
    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerUpdateAttributePacket(player.getId(), ShinsuQualityData.get(player).getAttribute()));
    }

}

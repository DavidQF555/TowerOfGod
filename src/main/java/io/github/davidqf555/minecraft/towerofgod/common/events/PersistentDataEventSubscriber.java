package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.CastingData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateCastingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PersistentDataEventSubscriber {

    private PersistentDataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (player instanceof ServerPlayer && target instanceof Player) {
            TowerOfGod.CHANNEL.send(new ServerUpdateAttributePacket(target.getId(), ShinsuQualityData.get(target).getAttribute()), PacketDistributor.PLAYER.with((ServerPlayer) player));
            TowerOfGod.CHANNEL.send(new ServerUpdateCastingPacket(target.getId(), CastingData.get((Player) target).isCasting()), PacketDistributor.PLAYER.with((ServerPlayer) player));
        }
    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        TowerOfGod.CHANNEL.send(new ServerUpdateAttributePacket(player.getId(), ShinsuQualityData.get(player).getAttribute()), PacketDistributor.PLAYER.with((ServerPlayer) player));
    }

}

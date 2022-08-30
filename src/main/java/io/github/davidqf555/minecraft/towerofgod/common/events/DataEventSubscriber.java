package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.SimpleCapabilityProvider;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataEventSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PREDICTED_QUALITY = new ResourceLocation(TowerOfGod.MOD_ID, "predicted_quality");

    private DataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Player) {
            event.addCapability(SHINSU_STATS, new SimpleCapabilityProvider<>(new ShinsuStats(), ShinsuStats.CAPABILITY));
            event.addCapability(PREDICTED_QUALITY, new SimpleCapabilityProvider<>(new PredictedShinsuQuality(), PredictedShinsuQuality.CAPABILITY));
        } else if (entity instanceof IShinsuUser) {
            event.addCapability(SHINSU_STATS, new SimpleCapabilityProvider<>(new ShinsuStats(), ShinsuStats.CAPABILITY));
        }
    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Entity entity = event.getEntity();
        ShinsuStats stats = ShinsuStats.get(entity);
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateClientAttributePacket(stats.getAttribute()));
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
    }

    @SubscribeEvent
    public static void onClonePlayerEvent(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            ServerPlayer resp = (ServerPlayer) event.getEntity();
            ShinsuStats.get(resp).deserializeNBT(ShinsuStats.get(original).serializeNBT());
            PredictedShinsuQuality.get(resp).deserializeNBT(PredictedShinsuQuality.get(original).serializeNBT());
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.SimpleCapabilityProvider;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.BaangsTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateBaangsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateUnlockedPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataEventSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation SHINSU_QUALITY = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_quality");
    private static final ResourceLocation SHINSU_TECHNIQUES = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques");
    private static final ResourceLocation PREDICTED_QUALITY = new ResourceLocation(TowerOfGod.MOD_ID, "predicted_quality");

    private DataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof IShinsuUser || entity instanceof Player) {
            event.addCapability(SHINSU_STATS, new SimpleCapabilityProvider<>(new ShinsuStats(), ShinsuStats.CAPABILITY));
            event.addCapability(SHINSU_QUALITY, new SimpleCapabilityProvider<>(new ShinsuQualityData(), ShinsuQualityData.CAPABILITY));
            if (entity instanceof Player) {
                event.addCapability(SHINSU_TECHNIQUES, new SimpleCapabilityProvider<>(new PlayerTechniqueData(), ShinsuTechniqueData.CAPABILITY));
                event.addCapability(PREDICTED_QUALITY, new SimpleCapabilityProvider<>(new PredictedShinsuQuality(), PredictedShinsuQuality.CAPABILITY));
            } else if (entity instanceof Mob) {
                event.addCapability(SHINSU_TECHNIQUES, new SimpleCapabilityProvider<>(new BaangsTechniqueData<>(), ShinsuTechniqueData.CAPABILITY));
            }
        }

    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ServerUpdateUnlockedPacket(PlayerTechniqueData.get(player).getUnlocked()));
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ServerUpdateBaangsPacket(PlayerTechniqueData.get(player).getBaangSettings()));
    }

    @SubscribeEvent
    public static void onClonePlayerEvent(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            original.reviveCaps();
            ServerPlayer resp = (ServerPlayer) event.getEntity();
            ShinsuStats.get(resp).deserializeNBT(ShinsuStats.get(original).serializeNBT());
            ShinsuTechniqueData.get(resp).deserializeNBT(ShinsuTechniqueData.get(original).serializeNBT());
            ShinsuQualityData.get(resp).deserializeNBT(ShinsuQualityData.get(original).serializeNBT());
            PredictedShinsuQuality.get(resp).deserializeNBT(PredictedShinsuQuality.get(original).serializeNBT());
            original.invalidateCaps();
        }
    }

}

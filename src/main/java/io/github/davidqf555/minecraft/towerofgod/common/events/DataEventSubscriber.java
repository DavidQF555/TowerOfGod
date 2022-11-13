package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.SimpleCapabilityProvider;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.CastingData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DataEventSubscriber {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation SHINSU_QUALITY = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_quality");
    private static final ResourceLocation SHINSU_TECHNIQUES = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques");
    private static final ResourceLocation PREDICTED_QUALITY = new ResourceLocation(TowerOfGod.MOD_ID, "predicted_quality");
    private static final ResourceLocation CASTING = new ResourceLocation(TowerOfGod.MOD_ID, "casting");

    private DataEventSubscriber() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof IShinsuUser || entity instanceof PlayerEntity) {
            event.addCapability(SHINSU_STATS, new SimpleCapabilityProvider<>(ShinsuStats.capability));
            event.addCapability(SHINSU_QUALITY, new SimpleCapabilityProvider<>(ShinsuQualityData.capability));
            event.addCapability(SHINSU_TECHNIQUES, new SimpleCapabilityProvider<>(ShinsuTechniqueData.capability));
            if (entity instanceof PlayerEntity) {
                event.addCapability(PREDICTED_QUALITY, new SimpleCapabilityProvider<>(PredictedShinsuQuality.capability));
                event.addCapability(CASTING, new SimpleCapabilityProvider<>(CastingData.capability));
            }
        }

    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Entity entity = event.getEntity();
        ShinsuStats stats = ShinsuStats.get(entity);
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateShinsuMeterPacket(ShinsuStats.getShinsu(entity), stats.getMaxShinsu()));
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateBaangsMeterPacket(ShinsuStats.getBaangs(entity), stats.getMaxBaangs()));
    }

    @SubscribeEvent
    public static void onClonePlayerEvent(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayerEntity original = (ServerPlayerEntity) event.getOriginal();
            ServerPlayerEntity resp = (ServerPlayerEntity) event.getPlayer();
            ShinsuStats.get(resp).deserializeNBT(ShinsuStats.get(original).serializeNBT());
            ShinsuTechniqueData.get(resp).deserializeNBT(ShinsuTechniqueData.get(original).serializeNBT());
            ShinsuQualityData.get(resp).deserializeNBT(ShinsuQualityData.get(original).serializeNBT());
            PredictedShinsuQuality.get(resp).deserializeNBT(PredictedShinsuQuality.get(original).serializeNBT());

        }
    }

}

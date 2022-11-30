package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.SimpleCapabilityProvider;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.RequirementTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.CastingData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
    private static final ResourceLocation CASTING = new ResourceLocation(TowerOfGod.MOD_ID, "casting");

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
                event.addCapability(CASTING, new SimpleCapabilityProvider<>(new CastingData(), CastingData.CAPABILITY));
            } else if (entity instanceof Mob) {
                event.addCapability(SHINSU_TECHNIQUES, new SimpleCapabilityProvider<>(new RequirementTechniqueData<>(), ShinsuTechniqueData.CAPABILITY));
            }
        }

    }

    @SubscribeEvent
    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        LivingEntity entity = event.getEntity();
        ShinsuStats stats = ShinsuStats.get(entity);
        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateShinsuMeterPacket(ShinsuStats.getShinsu(entity), stats.getMaxShinsu()));
    }

    @SubscribeEvent
    public static void onClonePlayerEvent(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            ServerPlayer resp = (ServerPlayer) event.getEntity();
            ShinsuStats.get(resp).deserializeNBT(ShinsuStats.get(original).serializeNBT());
            ShinsuTechniqueData.get(resp).deserializeNBT(ShinsuTechniqueData.get(original).serializeNBT());
            ShinsuQualityData.get(resp).deserializeNBT(ShinsuQualityData.get(original).serializeNBT());
            PredictedShinsuQuality.get(resp).deserializeNBT(PredictedShinsuQuality.get(original).serializeNBT());
        }
    }

}

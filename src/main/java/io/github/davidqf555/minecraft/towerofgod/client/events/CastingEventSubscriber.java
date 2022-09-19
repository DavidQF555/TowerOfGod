package io.github.davidqf555.minecraft.towerofgod.client.events;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateCastingPacket;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CastingEventSubscriber {

    private CastingEventSubscriber() {
    }

    @SubscribeEvent
    public static void preRenderLivingEvent(RenderLivingEvent.Pre<PlayerEntity, BipedModel<PlayerEntity>> event) {
        if (ClientReference.isCasting(event.getEntity())) {
            BipedModel<PlayerEntity> model = event.getRenderer().getModel();
            model.rightArm.visible = false;
            model.leftArm.visible = false;
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        if (player instanceof ServerPlayerEntity && target instanceof LivingEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerUpdateCastingPacket(target.getId(), ClientReference.isCasting((LivingEntity) target)));
        }
    }

}

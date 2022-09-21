package io.github.davidqf555.minecraft.towerofgod.client.events;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.model.CastingModelHelper;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CastingEventSubscriber {

    private static long prevGameTime = -1;

    private CastingEventSubscriber() {
    }

    @SubscribeEvent
    public static void preRenderPlayer(RenderPlayerEvent.Pre event) {
        PlayerEntity entity = event.getPlayer();
        if (ClientReference.isCasting(entity)) {
            BipedModel<AbstractClientPlayerEntity> model = event.getRenderer().getModel();
            model.rightArm.visible = false;
            model.leftArm.visible = false;
            long gameTime = entity.level.getGameTime();
            if (gameTime != prevGameTime) {
                CastingModelHelper.spawnParticles(entity, ShinsuAttribute.getParticles(ClientReference.getAttribute(entity)), 1);
                prevGameTime = gameTime;
            }
        }
    }

}

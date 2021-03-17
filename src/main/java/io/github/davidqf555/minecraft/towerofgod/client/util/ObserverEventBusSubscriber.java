package io.github.davidqf555.minecraft.towerofgod.client.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class ObserverEventBusSubscriber {

    public static final Map<UUID, List<UUID>> highlight = new HashMap<>();
    public static final Map<UUID, List<UUID>> stopHighlight = new HashMap<>();

    @SubscribeEvent
    public static void preRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        UUID id = entity.getUniqueID();
        boolean included = false;
        boolean stillGlowing = false;
        for (UUID key : highlight.keySet()) {
            List<UUID> values = highlight.get(key);
            List<UUID> stop = stopHighlight.containsKey(key) ? stopHighlight.get(key) : new ArrayList<>();
            if (stop.contains(id)) {
                values.remove(id);
                stop.remove(id);
                included = true;
            } else if (values.contains(id)) {
                stillGlowing = true;
                included = true;
            }
        }
        if (included) {
            entity.setGlowing(stillGlowing);
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {

        @SubscribeEvent
        public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            highlight.clear();
            stopHighlight.clear();
        }

    }
}

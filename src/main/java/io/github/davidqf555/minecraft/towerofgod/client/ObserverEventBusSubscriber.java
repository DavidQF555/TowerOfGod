package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
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

    public static final Map<UUID, Pair<Set<UUID>, Set<UUID>>> startStopHighlight = new HashMap<>();

    @SubscribeEvent
    public static void preRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        UUID id = entity.getUniqueID();
        boolean included = false;
        boolean stillGlowing = false;
        for (UUID key : new HashSet<>(startStopHighlight.keySet())) {
            Pair<Set<UUID>, Set<UUID>> pair = startStopHighlight.get(key);
            Set<UUID> highlight = pair.getFirst();
            Set<UUID> stop = pair.getSecond();
            if (stop.contains(id)) {
                highlight.remove(id);
                stop.remove(id);
                included = true;
                if (highlight.isEmpty()) {
                    startStopHighlight.remove(key);
                }
            } else if (highlight.contains(id)) {
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
            startStopHighlight.clear();
        }

    }
}

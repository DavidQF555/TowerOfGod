package io.github.davidqf555.minecraft.towerofgod.client.events;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

public final class ObserverEventBusSubscriber {

    public static final Map<UUID, Pair<Set<UUID>, Set<UUID>>> START_STOP_HIGHLIGHT = new HashMap<>();

    private ObserverEventBusSubscriber() {
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void preRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            LivingEntity entity = event.getEntity();
            UUID id = entity.getUUID();
            boolean included = false;
            boolean stillGlowing = false;
            for (UUID key : new HashSet<>(START_STOP_HIGHLIGHT.keySet())) {
                Pair<Set<UUID>, Set<UUID>> pair = START_STOP_HIGHLIGHT.get(key);
                Set<UUID> highlight = pair.getFirst();
                Set<UUID> stop = pair.getSecond();
                if (stop.contains(id)) {
                    highlight.remove(id);
                    stop.remove(id);
                    included = true;
                    if (highlight.isEmpty()) {
                        START_STOP_HIGHLIGHT.remove(key);
                    }
                } else if (highlight.contains(id)) {
                    stillGlowing = true;
                    included = true;
                }
            }
            if (included) {
                entity.setGlowingTag(stillGlowing);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            START_STOP_HIGHLIGHT.clear();
        }

    }
}

package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AdvancementEventSubscriber {

    private AdvancementEventSubscriber() {
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            AdvancementHolder advancement = event.getAdvancement();
            ResourceLocation id = advancement.id();
            advancement.value().parent().ifPresent(parent -> {
                if (parent.equals(ShinsuAttributeRegistry.ADVANCEMENT)) {
                    PredictedShinsuQuality.get(player).setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(getLocation(id)));
                    revokeAdvancement((ServerPlayer) player, advancement);
                } else if (parent.equals(ShinsuShapeRegistry.ADVANCEMENT)) {
                    PredictedShinsuQuality.get(player).setShape(ShinsuShapeRegistry.getRegistry().getValue(getLocation(id)));
                    revokeAdvancement((ServerPlayer) player, advancement);
                }
            });
        }
    }

    private static ResourceLocation getLocation(ResourceLocation advancement) {
        String[] split = advancement.getPath().split("/");
        return new ResourceLocation(advancement.getNamespace(), split[split.length - 1]);
    }

    private static void revokeAdvancement(ServerPlayer player, AdvancementHolder advancement) {
        advancement.value().criteria().keySet().forEach(criterion -> player.getAdvancements().revoke(advancement, criterion));
    }

}

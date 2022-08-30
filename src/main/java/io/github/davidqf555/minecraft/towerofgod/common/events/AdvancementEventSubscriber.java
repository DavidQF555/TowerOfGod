package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.advancements.Advancement;
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
        Advancement advancement = event.getAdvancement();
        Advancement parent = advancement.getParent();
        if (player instanceof ServerPlayer && parent != null) {
            if (parent.getId().equals(ShinsuAttributeRegistry.ADVANCEMENT)) {
                PredictedShinsuQuality.get(player).setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(getLocation(advancement.getId())));
                revokeAdvancement((ServerPlayer) player, advancement);
            } else if (parent.getId().equals(ShinsuShapeRegistry.ADVANCEMENT)) {
                PredictedShinsuQuality.get(player).setShape(ShinsuShapeRegistry.getRegistry().getValue(getLocation(advancement.getId())));
                revokeAdvancement((ServerPlayer) player, advancement);
            }
        }
    }

    private static ResourceLocation getLocation(ResourceLocation advancement) {
        String[] split = advancement.getPath().split("/");
        return new ResourceLocation(advancement.getNamespace(), split[split.length - 1]);
    }

    private static void revokeAdvancement(ServerPlayer player, Advancement advancement) {
        advancement.getCriteria().keySet().forEach(criterion -> {
            player.getAdvancements().revoke(advancement, criterion);
        });
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.PredictedShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AdvancementEventSubscriber {

    private AdvancementEventSubscriber() {
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        PlayerEntity player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        Advancement parent = advancement.getParent();
        if (player instanceof ServerPlayerEntity && parent != null) {
            if (parent.getId().equals(ShinsuAttributeRegistry.ADVANCEMENT)) {
                PredictedShinsuQuality.get(player).setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(getLocation(advancement.getId())));
                revokeAdvancement((ServerPlayerEntity) player, advancement);
            } else if (parent.getId().equals(ShinsuShapeRegistry.ADVANCEMENT)) {
                PredictedShinsuQuality.get(player).setShape(ShinsuShapeRegistry.getRegistry().getValue(getLocation(advancement.getId())));
                revokeAdvancement((ServerPlayerEntity) player, advancement);
            }
        }
    }

    private static ResourceLocation getLocation(ResourceLocation advancement) {
        String[] split = advancement.getPath().split("/");
        return new ResourceLocation(advancement.getNamespace(), split[split.length - 1]);
    }

    private static void revokeAdvancement(ServerPlayerEntity player, Advancement advancement) {
        advancement.getCriteria().keySet().forEach(criterion -> {
            player.getAdvancements().revoke(advancement, criterion);
        });
    }

}

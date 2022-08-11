package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EffectEventSubscriber {

    private EffectEventSubscriber() {
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        EffectInstance reinforcement = entity.getActivePotionEffect(EffectRegistry.BODY_REINFORCEMENT.get());
        if (reinforcement != null) {
            Vector3d motion = entity.getMotion().add(0, reinforcement.getAmplifier() * 0.025 + 0.025, 0);
            entity.setMotion(motion);
        }
        EffectInstance reverse = entity.getActivePotionEffect(EffectRegistry.REVERSE_FLOW.get());
        if (reverse != null) {
            Vector3d motion = entity.getMotion();
            entity.setMotion(motion.getX(), Math.max(0, motion.getY() - reverse.getAmplifier() * 0.025 - 0.025), motion.getZ());
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        EffectInstance effect = entity.getActivePotionEffect(EffectRegistry.BODY_REINFORCEMENT.get());
        if (effect != null) {
            event.setDistance(event.getDistance() - effect.getAmplifier() * 0.5f - 0.5f);
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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
        LivingEntity entity = event.getEntity();
        MobEffectInstance reinforcement = entity.getEffect(EffectRegistry.BODY_REINFORCEMENT.get());
        if (reinforcement != null) {
            Vec3 motion = entity.getDeltaMovement().add(0, reinforcement.getAmplifier() * 0.025 + 0.025, 0);
            entity.setDeltaMovement(motion);
        }
        MobEffectInstance reverse = entity.getEffect(EffectRegistry.REVERSE_FLOW.get());
        if (reverse != null) {
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x(), Math.max(0, motion.y() - reverse.getAmplifier() * 0.025 - 0.025), motion.z());
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(EffectRegistry.BODY_REINFORCEMENT.get());
        if (effect != null) {
            event.setDistance(event.getDistance() - effect.getAmplifier() * 0.5f - 0.5f);
        }
    }

}

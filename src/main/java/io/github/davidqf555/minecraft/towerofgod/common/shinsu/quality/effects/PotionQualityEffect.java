package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;

public class PotionQualityEffect implements ShinsuQualityEffect<EntityRayTraceResult> {

    private final EffectInstance effect;

    public PotionQualityEffect(EffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public void apply(Entity user, EntityRayTraceResult clip) {
        Entity entity = clip.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(effect);
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;

public class PotionAttributeEffect implements ShinsuAttributeEffect<EntityRayTraceResult> {

    private final EffectInstance effect;

    public PotionAttributeEffect(EffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public void apply(Entity user, EntityRayTraceResult clip) {
        Entity entity = clip.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(effect);
        }
    }

}

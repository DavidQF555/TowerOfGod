package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class PotionAttributeEffect implements ShinsuAttributeEffect<EntityHitResult> {

    private final MobEffectInstance effect;

    public PotionAttributeEffect(MobEffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public void apply(Entity user, EntityHitResult clip) {
        Entity entity = clip.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(effect);
        }
    }

}

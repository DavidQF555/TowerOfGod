package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class BodyReinforcement extends ShinsuTechnique {

    public BodyReinforcement(LivingEntity user, int level) {
        super(ShinsuTechniques.BODY_REINFORCEMENT, user, level);
    }

    @Override
    public void tick(World world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            user.addPotionEffect(new EffectInstance(Effect.get(11), 2, getLevel() / 3, true, false, false));
            user.addPotionEffect(new EffectInstance(Effect.get(1), 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effect.get(3), 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effect.get(5), 2, getLevel() / 2, true, false, false));
            user.addPotionEffect(new EffectInstance(Effect.get(8), 2, getLevel(), true, false, false));
        }
    }
}

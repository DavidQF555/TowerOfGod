package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
            user.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 2, getLevel() / 3, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.SPEED, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.HASTE, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.STRENGTH, 2, getLevel() / 2, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 2, getLevel(), true, false, false));
        }
    }
}

package com.davidqf.minecraft.towerofgod.common.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BodyReinforcement extends ShinsuTechniqueInstance {

    public BodyReinforcement(LivingEntity user, int level) {
        super(ShinsuTechnique.BODY_REINFORCEMENT, user, level, level * 300);
    }

    @Override
    public void tick(World world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            user.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.SPEED, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.HASTE, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.STRENGTH, 2, getLevel(), true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 2, getLevel(), true, false, false));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getLevel() * 300;
    }

    public static class Builder implements ShinsuTechnique.Builder<BodyReinforcement> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public BodyReinforcement build(@Nonnull LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return new BodyReinforcement(user, level);
        }

        @Nonnull
        @Override
        public BodyReinforcement emptyBuild() {
            return new BodyReinforcement(null, 0);
        }

        @Override
        public int getShinsuUse() {
            return shinsu;
        }

        @Override
        public int getBaangUse() {
            return baangs;
        }
    }
}

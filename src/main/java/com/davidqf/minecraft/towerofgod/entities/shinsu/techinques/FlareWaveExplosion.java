package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FlareWaveExplosion extends ShinsuTechnique.Targetable {

    private static final double RANGE = 1.5;
    private static final float DAMAGE = 10;
    private static final double KNOCKBACK = 3;

    public FlareWaveExplosion(LivingEntity user, int level, @Nonnull LivingEntity target) {
        super(ShinsuTechniques.FLARE_WAVE_EXPLOSION, user, level, target);
    }

    @Override
    public void onUse(World world) {
        Entity u = getUser(world);
        Entity t = getTarget(world);
        if (u instanceof LivingEntity && t instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) u;
            LivingEntity target = (LivingEntity) t;
            double resistance = ShinsuTechnique.getTotalResistance(user, target);
            target.attackEntityFrom(DamageSource.MAGIC, (float) (DAMAGE * resistance) * getLevel() / 2);
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (60 * resistance), getLevel(), true, false, false));
            double knock = KNOCKBACK * resistance;
            Vector3d vel = target.getPositionVec().subtract(user.getPositionVec()).normalize().mul(knock, knock, knock);
            target.addVelocity(vel.getX(), vel.getY(), vel.getZ());
        }
    }

    @Override
    public boolean canUse(World world) {
        Entity u = getUser(world);
        Entity target = getTarget(world);
        if (u != null && target != null) {
            return super.canUse(world) && u.getDistanceSq(target) <= RANGE * RANGE;
        }
        return false;
    }

}

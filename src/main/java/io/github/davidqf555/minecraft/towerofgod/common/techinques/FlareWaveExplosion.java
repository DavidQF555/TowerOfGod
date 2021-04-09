package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FlareWaveExplosion extends ShinsuTechniqueInstance.Targetable {

    private static final double RANGE = 3;
    private static final float DAMAGE = 10;
    private static final double KNOCKBACK = 2;

    public FlareWaveExplosion(LivingEntity user, int level, LivingEntity target) {
        super(ShinsuTechnique.FLARE_WAVE_EXPLOSION, user, level, target, 0);
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        Entity t = getTarget(world);
        if (user != null && t instanceof LivingEntity && user.getDistanceSq(t) <= RANGE * RANGE) {
            LivingEntity target = (LivingEntity) t;
            double resistance = IShinsuStats.getTotalResistance(world, user, target);
            target.attackEntityFrom(DamageSource.MAGIC, (float) (DAMAGE / resistance) * getLevel() / 2);
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (60 / resistance), getLevel(), true, false, false));
            double knock = KNOCKBACK / resistance;
            Vector3d vel = target.getPositionVec().subtract(user.getPositionVec()).normalize().mul(knock, knock, knock);
            target.addVelocity(vel.getX(), vel.getY(), vel.getZ());
        }
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    public static class Builder implements ShinsuTechnique.Builder<FlareWaveExplosion> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public FlareWaveExplosion build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return target instanceof LivingEntity ? new FlareWaveExplosion(user, level, (LivingEntity) target) : null;
        }

        @Nonnull
        @Override
        public FlareWaveExplosion emptyBuild() {
            return new FlareWaveExplosion(null, 0, null);
        }

        @Override
        public boolean canCast(ShinsuTechnique technique, LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return ShinsuTechnique.Builder.super.canCast(technique, user, level, target, dir) && target instanceof LivingEntity && user.getDistanceSq(target) <= RANGE * RANGE;
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

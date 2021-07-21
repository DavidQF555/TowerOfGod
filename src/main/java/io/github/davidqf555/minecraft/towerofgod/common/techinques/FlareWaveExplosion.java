package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
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

    private static final double RANGE = 1;
    private static final int COOLDOWN = 600;
    private static final float DAMAGE = 10;

    public FlareWaveExplosion(LivingEntity user, int level, LivingEntity target) {
        super(null, user, level, target, 0);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.FLARE_WAVE_EXPLOSION;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        Entity t = getTarget(world);
        if (user != null && t instanceof LivingEntity && user.getDistanceSq(t) <= RANGE * RANGE) {
            LivingEntity target = (LivingEntity) t;
            double resistance = ShinsuStats.getNetResistance(world, user, target);
            target.attackEntityFrom(DamageSource.MAGIC, (float) (DAMAGE / resistance) * getLevel() / 2);
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (60 / resistance), getLevel(), true, false, false));
        }
    }

    @Override
    public int getCooldown() {
        return COOLDOWN;
    }

    @Override
    public int getShinsuUse() {
        return 20;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    public static class Builder implements ShinsuTechnique.IBuilder<FlareWaveExplosion> {

        @Override
        public FlareWaveExplosion build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return target instanceof LivingEntity && user.getDistanceSq(target) <= RANGE * RANGE ? new FlareWaveExplosion(user, level, (LivingEntity) target) : null;
        }

        @Nonnull
        @Override
        public FlareWaveExplosion emptyBuild() {
            return new FlareWaveExplosion(null, 0, null);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.FLARE_WAVE_EXPLOSION;
        }
    }

}

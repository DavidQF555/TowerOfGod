package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FlareWaveExplosion extends ShinsuTechniqueInstance.Targetable {

    private static final double RANGE = 1;

    public FlareWaveExplosion(LivingEntity user, int level, LivingEntity target) {
        super(user, level, target);
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
            target.attackEntityFrom(DamageSource.MAGIC, (float) (5 / resistance) * getLevel() / 2);
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (60 / resistance), getLevel(), true, false, false));
        }
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public int getShinsuUse() {
        return getLevel() * 3 + 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FlareWaveExplosion> {

        @Override
        public Either<FlareWaveExplosion, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return target instanceof LivingEntity && user.getDistanceSq(target) <= RANGE * RANGE ? Either.left(new FlareWaveExplosion(user, level, (LivingEntity) target)) : Either.right(ErrorMessages.REQUIRES_TARGET.apply(RANGE));
        }

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

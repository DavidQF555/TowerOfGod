package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ReverseFlowControl extends ShinsuTechniqueInstance.Targetable {

    private static final double RANGE = 4;
    private static final int BASE_DURATION = 40;

    public ReverseFlowControl(LivingEntity user, int level, LivingEntity target) {
        super(ShinsuTechnique.REVERSE_FLOW_CONTROL, user, level, target, level * BASE_DURATION);
    }

    @Override
    public void tick(ServerWorld world) {
        Entity user = getUser(world);
        Entity target = getTarget(world);
        if (user != null && target instanceof LivingEntity) {
            if (user.getDistanceSq(target) > RANGE * RANGE) {
                remove(world);
                return;
            }
            double resistance = IShinsuStats.getTotalResistance(world, user, target);
            ((LivingEntity) target).addPotionEffect(new EffectInstance(RegistryHandler.REVERSE_FLOW_EFFECT.get(), 2, (int) (1.5 * resistance * getLevel())));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return BASE_DURATION * 2;
    }

    public static class Builder implements ShinsuTechnique.Builder<ReverseFlowControl> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public ReverseFlowControl build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return target instanceof LivingEntity ? new ReverseFlowControl(user, level, (LivingEntity) target) : null;
        }

        @Nonnull
        @Override
        public ReverseFlowControl emptyBuild() {
            return new ReverseFlowControl(null, 0, null);
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

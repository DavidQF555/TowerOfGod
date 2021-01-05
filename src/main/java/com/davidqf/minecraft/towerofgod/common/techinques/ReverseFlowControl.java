package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReverseFlowControl extends ShinsuTechniqueInstance.Targetable {

    private static final double RANGE = 2;

    public ReverseFlowControl(LivingEntity user, int level, @Nonnull LivingEntity target) {
        super(ShinsuTechnique.REVERSE_FLOW_CONTROL, user, level, target, level * 40);
    }

    @Override
    public void tick(ServerWorld world) {
        Entity u = getUser(world);
        Entity t = getTarget(world);
        if (u instanceof LivingEntity && t instanceof LivingEntity) {
            if (u.getDistanceSq(t) > RANGE * RANGE) {
                remove(world);
                return;
            }
            LivingEntity user = (LivingEntity) u;
            LivingEntity target = (LivingEntity) t;
            double resistance = getTotalResistance(user, target);
            target.addPotionEffect(new EffectInstance(RegistryHandler.REVERSE_FLOW_EFFECT.get(), 2, (int) (1.5 * resistance * getLevel())));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return 80;
    }

    public static class Builder implements ShinsuTechnique.Builder<ReverseFlowControl> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public ReverseFlowControl build(@Nonnull LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return target instanceof LivingEntity ? new ReverseFlowControl(user, level, (LivingEntity) target) : null;
        }

        @Nonnull
        @Override
        public ReverseFlowControl emptyBuild() {
            return new ReverseFlowControl(null, 0, null);
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

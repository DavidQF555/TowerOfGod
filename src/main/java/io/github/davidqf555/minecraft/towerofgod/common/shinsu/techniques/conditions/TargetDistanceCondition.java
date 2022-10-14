package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

public class TargetDistanceCondition extends HasTargetCondition {

    private final double min;
    private final double max;

    protected TargetDistanceCondition(double min, double max) {
        super(true);
        this.min = min;
        this.max = max;
    }

    public static TargetDistanceCondition above(double min) {
        return new TargetDistanceCondition(min, 0);
    }

    public static TargetDistanceCondition from(double min, double max) {
        return new TargetDistanceCondition(min, max);
    }

    public static TargetDistanceCondition below(double max) {
        return new TargetDistanceCondition(0, max);
    }

    @Override
    public boolean shouldUse(MobEntity entity) {
        if (super.shouldUse(entity)) {
            LivingEntity target = entity.getTarget();
            double distSq = entity.distanceToSqr(target);
            return distSq >= min * min && (max <= 0 || distSq <= max * max);
        }
        return false;
    }
}

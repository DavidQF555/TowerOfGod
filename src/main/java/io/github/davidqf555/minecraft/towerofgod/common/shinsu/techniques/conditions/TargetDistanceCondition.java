package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.MobUseConditionRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetDistanceCondition extends HasTargetCondition {

    public static final Codec<TargetDistanceCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.optionalFieldOf("min", 0.0).forGetter(cond -> cond.min),
            Codec.DOUBLE.optionalFieldOf("max", Double.MAX_VALUE).forGetter(cond -> cond.max)
    ).apply(inst, TargetDistanceCondition::new));
    private final double min;
    private final double max;

    public TargetDistanceCondition(double min, double max) {
        super(true);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean shouldUse(Mob entity) {
        if (super.shouldUse(entity)) {
            LivingEntity target = entity.getTarget();
            double distSq = entity.distanceToSqr(target);
            return distSq >= min * min && (max <= 0 || distSq <= max * max);
        }
        return false;
    }

    @Override
    public MobUseConditionType getType() {
        return MobUseConditionRegistry.TARGET_DISTANCE.get();
    }

}

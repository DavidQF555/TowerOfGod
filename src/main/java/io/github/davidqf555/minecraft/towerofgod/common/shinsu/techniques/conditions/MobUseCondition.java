package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.entity.MobEntity;

public interface MobUseCondition {

    MobUseCondition TRUE = entity -> true;

    static CombinationCondition and(MobUseCondition... conditions) {
        return new CombinationCondition(conditions);
    }

    boolean shouldUse(MobEntity entity);

}

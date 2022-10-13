package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.entity.MobEntity;

public class HasTargetCondition implements MobUseCondition {

    private final boolean has;

    public HasTargetCondition(boolean has) {
        this.has = has;
    }

    @Override
    public boolean shouldUse(MobEntity entity) {
        return entity.getTarget() == null && !has || entity.getTarget() != null && has;
    }

}

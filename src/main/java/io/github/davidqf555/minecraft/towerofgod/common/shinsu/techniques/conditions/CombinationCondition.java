package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.world.entity.Mob;

public class CombinationCondition implements MobUseCondition {

    private final MobUseCondition[] conditions;

    protected CombinationCondition(MobUseCondition... conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean shouldUse(Mob entity) {
        for (MobUseCondition condition : conditions) {
            if (!condition.shouldUse(entity)) {
                return false;
            }
        }
        return true;
    }
}

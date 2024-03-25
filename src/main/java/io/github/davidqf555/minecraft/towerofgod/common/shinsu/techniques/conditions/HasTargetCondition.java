package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.MobUseConditionRegistry;
import net.minecraft.world.entity.Mob;

public class HasTargetCondition implements MobUseCondition {

    public static final Codec<HasTargetCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("has").forGetter(cond -> cond.has)
    ).apply(inst, HasTargetCondition::new));
    private final boolean has;

    public HasTargetCondition(boolean has) {
        this.has = has;
    }

    @Override
    public boolean shouldUse(Mob entity) {
        return (entity.getTarget() != null) == has;
    }

    @Override
    public MobUseConditionType getType() {
        return MobUseConditionRegistry.HAS_TARGET.get();
    }

}

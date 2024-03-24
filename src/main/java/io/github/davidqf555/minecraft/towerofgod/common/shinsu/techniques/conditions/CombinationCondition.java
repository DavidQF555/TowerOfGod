package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.MobUseConditionRegistry;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.function.Supplier;

public class CombinationCondition implements MobUseCondition {

    public static final Supplier<Codec<CombinationCondition>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
            MobUseCondition.CODEC.get().listOf().fieldOf("all").forGetter(cond -> cond.conditions)
    ).apply(inst, CombinationCondition::new)));
    private final List<MobUseCondition> conditions;

    protected CombinationCondition(List<MobUseCondition> conditions) {
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

    @Override
    public MobUseConditionType getType() {
        return MobUseConditionRegistry.COMBINATION.get();
    }

}

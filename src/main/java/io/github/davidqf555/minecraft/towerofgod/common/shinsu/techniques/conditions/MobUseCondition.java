package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.MobUseConditionRegistry;
import net.minecraft.world.entity.Mob;

import java.util.function.Supplier;

public interface MobUseCondition {

    Supplier<Codec<MobUseCondition>> CODEC = Suppliers.memoize(() -> MobUseConditionRegistry.getRegistry().getCodec().dispatch(MobUseCondition::getType, MobUseConditionType::getCodec));
    MobUseCondition ALWAYS = new MobUseCondition() {
        @Override
        public boolean shouldUse(Mob entity) {
            return true;
        }

        @Override
        public MobUseConditionType getType() {
            return MobUseConditionRegistry.ALWAYS.get();
        }
    };

    boolean shouldUse(Mob entity);

    MobUseConditionType getType();

}

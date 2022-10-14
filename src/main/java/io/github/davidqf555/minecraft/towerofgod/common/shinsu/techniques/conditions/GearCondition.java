package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class GearCondition implements MobUseCondition {

    private final EquipmentSlot slot;
    private final Predicate<ItemStack> condition;

    public GearCondition(EquipmentSlot slot, Predicate<ItemStack> condition) {
        this.slot = slot;
        this.condition = condition;
    }

    @Override
    public boolean shouldUse(Mob entity) {
        return condition.test(entity.getItemBySlot(slot));
    }

}

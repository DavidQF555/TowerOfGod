package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class GearCondition implements MobUseCondition {

    private final EquipmentSlotType slot;
    private final Predicate<ItemStack> condition;

    public GearCondition(EquipmentSlotType slot, Predicate<ItemStack> condition) {
        this.slot = slot;
        this.condition = condition;
    }

    @Override
    public boolean shouldUse(MobEntity entity) {
        return condition.test(entity.getItemBySlot(slot));
    }

}

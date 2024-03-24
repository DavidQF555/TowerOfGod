package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.MobUseConditionRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

public class GearCondition implements MobUseCondition {

    public static final Codec<GearCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(EquipmentSlot::byName, EquipmentSlot::getName).fieldOf("slot").forGetter(cond -> cond.slot),
            Codec.BOOL.fieldOf("gear").forGetter(cond -> cond.gear)
    ).apply(inst, GearCondition::new));
    private final EquipmentSlot slot;
    private final boolean gear;

    public GearCondition(EquipmentSlot slot, boolean gear) {
        this.slot = slot;
        this.gear = gear;

    }

    @Override
    public boolean shouldUse(Mob entity) {
        return entity.getItemBySlot(slot).isEmpty() != gear;
    }

    @Override
    public MobUseConditionType getType() {
        return MobUseConditionRegistry.GEAR.get();
    }

}

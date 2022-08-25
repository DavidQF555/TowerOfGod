package io.github.davidqf555.minecraft.towerofgod.common.effects;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class BodyReinforcementEffect extends Effect {

    private static final int COLOR = 0xAA24a6d1;
    private static final String ID = "ecd380c6-ffd3-4d77-8752-fc0109c1aa7a";

    public BodyReinforcementEffect() {
        super(EffectType.BENEFICIAL, COLOR);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ID, 0.075, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, ID, 0.0375, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, ID, 0.125, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ID, 0.05, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.ARMOR, ID, 0.5, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.FLYING_SPEED, ID, 0.05, AttributeModifier.Operation.MULTIPLY_BASE);
        addAttributeModifier(Attributes.JUMP_STRENGTH, ID, 0.05, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}

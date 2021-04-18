package io.github.davidqf555.minecraft.towerofgod.common.effects;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class BodyReinforcementEffect extends Effect {

    private static final int COLOR = 0xFF696969;
    private static final String ID = "ecd380c6-ffd3-4d77-8752-fc0109c1aa7a";

    public BodyReinforcementEffect() {
        super(EffectType.BENEFICIAL, COLOR);
        addAttributesModifier(Attributes.MOVEMENT_SPEED, ID, 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributesModifier(Attributes.ATTACK_SPEED, ID, 0.75, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributesModifier(Attributes.ATTACK_DAMAGE, ID, 0.25, AttributeModifier.Operation.ADDITION);
        addAttributesModifier(Attributes.KNOCKBACK_RESISTANCE, ID, 0.1, AttributeModifier.Operation.ADDITION);
        addAttributesModifier(Attributes.ARMOR, ID, 1, AttributeModifier.Operation.ADDITION);
        addAttributesModifier(Attributes.FLYING_SPEED, ID, 0.1, AttributeModifier.Operation.MULTIPLY_BASE);
        addAttributesModifier(Attributes.HORSE_JUMP_STRENGTH, ID, 0.1, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}

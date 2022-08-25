package io.github.davidqf555.minecraft.towerofgod.common.effects;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ReverseFlowEffect extends Effect {

    private static final int COLOR = 0xFFFF0000;
    private static final String ID = "aec9659b-3618-43f6-b603-4cd3f14c060b";

    public ReverseFlowEffect() {
        super(EffectType.HARMFUL, COLOR);
        addAttributeModifier(Attributes.ATTACK_SPEED, ID, -0.4, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.FLYING_SPEED, ID, -0.01, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.JUMP_STRENGTH, ID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ID, -0.01, AttributeModifier.Operation.ADDITION);
    }
}

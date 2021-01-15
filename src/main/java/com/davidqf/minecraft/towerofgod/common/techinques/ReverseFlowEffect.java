package com.davidqf.minecraft.towerofgod.common.techinques;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ReverseFlowEffect extends Effect {

    private static final int COLOR = 0xFFFF0000;
    private static final String ID = "aec9659b-3618-43f6-b603-4cd3f14c060b";

    public ReverseFlowEffect() {
        super(EffectType.HARMFUL, COLOR);
        addAttributesModifier(Attributes.ATTACK_SPEED, ID, -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributesModifier(Attributes.FLYING_SPEED, ID, -0.2, AttributeModifier.Operation.ADDITION);
        addAttributesModifier(Attributes.HORSE_JUMP_STRENGTH, ID, -0.2, AttributeModifier.Operation.ADDITION);
        addAttributesModifier(Attributes.MOVEMENT_SPEED, ID, -0.2, AttributeModifier.Operation.ADDITION);
    }
}

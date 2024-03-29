package io.github.davidqf555.minecraft.towerofgod.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ReverseFlowEffect extends MobEffect {

    private static final int COLOR = 0xFFFF0000;
    private static final String ID = "aec9659b-3618-43f6-b603-4cd3f14c060b";

    public ReverseFlowEffect() {
        super(MobEffectCategory.HARMFUL, COLOR);
        addAttributeModifier(Attributes.ATTACK_SPEED, ID, -0.4, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.FLYING_SPEED, ID, -0.01, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.JUMP_STRENGTH, ID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ID, -0.01, AttributeModifier.Operation.ADDITION);
    }
}

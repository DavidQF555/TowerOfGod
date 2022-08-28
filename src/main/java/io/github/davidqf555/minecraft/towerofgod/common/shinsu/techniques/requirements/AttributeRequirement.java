package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class AttributeRequirement implements IRequirement {

    private final ShinsuAttribute attribute;

    public AttributeRequirement(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getAttribute() == attribute;
    }

    @Override
    public Component getText() {
        return attribute == null ? Messages.REQUIRES_NO_ATTRIBUTE : Messages.getRequiresAttribute(attribute);
    }
}

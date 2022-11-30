package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class AttributeRequirement implements IRequirement {

    private final ShinsuAttribute attribute;

    public AttributeRequirement(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean isUnlocked(Entity user) {
        return ShinsuQualityData.get(user).getAttribute() == attribute;
    }

    @Override
    public ITextComponent getText() {
        return attribute == null ? Messages.REQUIRES_NO_ATTRIBUTE : Messages.getRequiresAttribute(attribute);
    }
}

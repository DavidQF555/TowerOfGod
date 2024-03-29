package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class AttributeRequirement implements IRequirement {

    private final Supplier<ShinsuAttribute> attribute;

    public AttributeRequirement(Supplier<ShinsuAttribute> attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean isUnlocked(Entity user) {
        return ShinsuQualityData.get(user).getAttribute() == attribute.get();
    }

    @Override
    public Component getText() {
        ShinsuAttribute attribute = this.attribute.get();
        return attribute == null ? Messages.REQUIRES_NO_ATTRIBUTE : Messages.getRequiresAttribute(attribute);
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class ShapeRequirement implements IRequirement {

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getShape() != null;
    }

    @Override
    public Component getText() {
        return Messages.REQUIRES_SHAPE;
    }
}

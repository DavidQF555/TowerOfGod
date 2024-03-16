package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ShapeRequirement implements IRequirement {

    @Override
    public boolean isUnlocked(Entity user) {
        return ShinsuQualityData.get(user).getShape() != null;
    }

    @Override
    public Component getText() {
        return Component.empty();
    }
}

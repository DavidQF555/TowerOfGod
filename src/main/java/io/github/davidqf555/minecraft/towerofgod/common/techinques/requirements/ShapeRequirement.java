package io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class ShapeRequirement implements IRequirement {

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getShape() != ShinsuShape.NONE;
    }

    @Override
    public ITextComponent getText() {
        return Messages.REQUIRES_SHAPE;
    }
}

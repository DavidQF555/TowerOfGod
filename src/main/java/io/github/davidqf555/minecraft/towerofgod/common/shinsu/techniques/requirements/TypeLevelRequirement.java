package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class TypeLevelRequirement implements IRequirement {

    private final ShinsuTechniqueType type;
    private final int level;

    public TypeLevelRequirement(ShinsuTechniqueType type, int level) {
        this.type = type;
        this.level = level;
    }

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getData(type).getLevel() >= level;
    }

    @Override
    public ITextComponent getText() {
        return Messages.getRequiresLevel(type, level);
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class QualityRequirement implements IRequirement {

    private final ShinsuQuality quality;

    public QualityRequirement(@Nullable ShinsuQuality quality) {
        this.quality = quality;
    }

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getQuality() == quality;
    }

    @Override
    public ITextComponent getText() {
        return quality == null ? Messages.REQUIRES_NO_QUALITY : Messages.REQUIRES_QUALITY.apply(quality);
    }
}

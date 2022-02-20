package io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class QualityRequirement implements IRequirement {

    private final ShinsuQuality quality;

    public QualityRequirement(ShinsuQuality quality) {
        this.quality = quality;
    }

    @Override
    public boolean isUnlocked(LivingEntity user) {
        return ShinsuStats.get(user).getQuality() == quality;
    }

    @Override
    public ITextComponent getText() {
        return Messages.REQUIRES_QUALITY.apply(quality);
    }
}

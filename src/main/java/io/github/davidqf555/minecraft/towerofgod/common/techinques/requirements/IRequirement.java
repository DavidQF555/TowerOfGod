package io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public interface IRequirement {

    boolean isUnlocked(LivingEntity user);

    ITextComponent getText();

}

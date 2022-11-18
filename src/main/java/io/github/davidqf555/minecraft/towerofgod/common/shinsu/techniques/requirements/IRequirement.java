package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;

public interface IRequirement {

    boolean isUnlocked(Entity user);

    ITextComponent getText();

}

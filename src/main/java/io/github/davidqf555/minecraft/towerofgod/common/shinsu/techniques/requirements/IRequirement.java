package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public interface IRequirement {

    boolean isUnlocked(LivingEntity user);

    Component getText();

}

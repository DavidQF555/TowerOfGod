package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public interface IRequirement {

    boolean isUnlocked(Entity user);

    Component getText();

}

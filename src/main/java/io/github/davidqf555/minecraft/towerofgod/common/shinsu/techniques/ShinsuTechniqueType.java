package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.chat.TranslatableComponent;

public enum ShinsuTechniqueType {
    DISRUPTION,
    CONTROL,
    MANIFEST,
    DEVICE_CONTROL;

    private final TranslatableComponent text;

    ShinsuTechniqueType() {
        text = new TranslatableComponent("type." + TowerOfGod.MOD_ID + "." + name().toLowerCase());
    }

    public TranslatableComponent getText() {
        return text;
    }
}

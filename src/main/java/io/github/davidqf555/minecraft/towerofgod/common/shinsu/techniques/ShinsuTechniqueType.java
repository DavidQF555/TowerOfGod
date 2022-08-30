package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ShinsuTechniqueType {
    DISRUPTION,
    CONTROL,
    MANIFEST,
    DEVICE_CONTROL;

    private final MutableComponent text;

    ShinsuTechniqueType() {
        text = Component.translatable("type." + TowerOfGod.MOD_ID + "." + name().toLowerCase());
    }

    public MutableComponent getText() {
        return text;
    }
}

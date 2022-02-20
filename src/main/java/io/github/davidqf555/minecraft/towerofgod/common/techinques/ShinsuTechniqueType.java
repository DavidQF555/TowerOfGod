package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.util.text.TranslationTextComponent;

public enum ShinsuTechniqueType {
    DISRUPTION,
    CONTROL,
    MANIFEST,
    DEVICE_CONTROL;

    private final TranslationTextComponent text;

    ShinsuTechniqueType() {
        text = new TranslationTextComponent("type." + TowerOfGod.MOD_ID + "." + name().toLowerCase());
    }

    public TranslationTextComponent getText() {
        return text;
    }
}

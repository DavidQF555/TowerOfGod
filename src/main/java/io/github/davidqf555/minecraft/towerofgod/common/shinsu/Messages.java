package io.github.davidqf555.minecraft.towerofgod.common.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class Messages {

    public static final TranslationTextComponent REQUIRES_DEVICE = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_device");
    public static final TranslationTextComponent REQUIRES_SHAPE = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shape");
    public static final TranslationTextComponent LOCKED = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.locked");
    public static final TranslationTextComponent REQUIRES_NO_QUALITY = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_no_quality");

    private Messages() {
    }

    public static ITextComponent getOnCooldown(double cooldown) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.on_cooldown", cooldown);
    }

    public static ITextComponent getRequiresShinsu(int requirement) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shinsu", requirement);
    }

    public static ITextComponent getRequiresBaangs(int requirement) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_baangs", requirement);
    }

    public static ITextComponent getRequiresLevel(ShinsuTechniqueType type, int requirement) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_level", requirement, type.getText());
    }

    public static TranslationTextComponent getRequiresTarget(double range) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_target", range);
    }

    public static TranslationTextComponent getRequiresQuality(ShinsuQuality quality) {
        return new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_quality", quality.getName());
    }
}

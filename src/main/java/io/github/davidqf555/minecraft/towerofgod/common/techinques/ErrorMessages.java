package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class ErrorMessages {

    public static final Function<Integer, ITextComponent> ON_COOLDOWN = cooldown -> new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.on_cooldown", cooldown);
    public static final Function<Integer, ITextComponent> REQUIRES_SHINSU = requirement -> new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shinsu", requirement);
    public static final Function<Integer, ITextComponent> REQUIRES_BAANGS = requirement -> new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_baangs", requirement);
    public static final BiFunction<ShinsuTechniqueType, Integer, ITextComponent> REQUIRES_LEVEL = (type, requirement) -> new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_level", requirement, type.getText());
    public static final TranslationTextComponent REQUIRES_DEVICE = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_device");
    public static final Function<Double, TranslationTextComponent> REQUIRES_TARGET = range -> new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_target", range);
    public static final TranslationTextComponent REQUIRES_SHAPE = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shape");

    private ErrorMessages() {
    }
}
package io.github.davidqf555.minecraft.towerofgod.common.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public final class Messages {

    public static final TranslatableComponent REQUIRES_DEVICE = new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_device");
    public static final TranslatableComponent REQUIRES_SHAPE = new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shape");
    public static final TranslatableComponent LOCKED = new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.locked");
    public static final TranslatableComponent REQUIRES_NO_ATTRIBUTE = new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_no_attribute");

    private Messages() {
    }

    public static Component getOnCooldown(double cooldown) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.on_cooldown", cooldown);
    }

    public static Component getRequiresShinsu(int requirement) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_shinsu", requirement);
    }

    public static Component getRequiresBaangs(int requirement) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_baangs", requirement);
    }

    public static Component getRequiresLevel(ShinsuTechniqueType type, int requirement) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_level", requirement, type.getText());
    }

    public static TranslatableComponent getRequiresTarget(double range) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_target", range);
    }

    public static TranslatableComponent getRequiresAttribute(ShinsuAttribute attribute) {
        return new TranslatableComponent("technique." + TowerOfGod.MOD_ID + ".error.requires_attribute", attribute.getName());
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Messages {

    public static final MutableComponent REQUIRES_DEVICE = Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_device");
    public static final MutableComponent REQUIRES_SHAPE = Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_shape");
    public static final MutableComponent LOCKED = Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.locked");
    public static final MutableComponent REQUIRES_NO_ATTRIBUTE = Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_no_attribute");

    private Messages() {
    }

    public static Component getOnCooldown(double cooldown) {
        return Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.on_cooldown", cooldown);
    }

    public static Component getRequiresShinsu(int requirement) {
        return Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_shinsu", requirement);
    }

    public static MutableComponent getRequiresTarget(double range) {
        return Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_target", range);
    }

    public static MutableComponent getRequiresAttribute(ShinsuAttribute attribute) {
        return Component.translatable("technique." + TowerOfGod.MOD_ID + ".error.requires_attribute", attribute.getName());
    }
}

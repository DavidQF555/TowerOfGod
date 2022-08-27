package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ShinsuItemColor implements IItemColor {

    @Override
    public int getColor(@Nonnull ItemStack item, int tintIndex) {
        return ShinsuAttribute.getColor(ShinsuAttribute.getAttribute(item));
    }

}

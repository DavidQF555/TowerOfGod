package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.items.DeviceItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class DeviceItemColor implements IItemColor {

    @Override
    public int getColor(ItemStack item, int tintIndex) {
        return ((DeviceItem) item.getItem()).getColor(item).getColorValue();
    }
}

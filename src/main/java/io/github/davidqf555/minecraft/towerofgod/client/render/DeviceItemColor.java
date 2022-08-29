package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.items.DeviceItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class DeviceItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack item, int tintIndex) {
        return ((DeviceItem) item.getItem()).getColor(item).getTextColor();
    }

}

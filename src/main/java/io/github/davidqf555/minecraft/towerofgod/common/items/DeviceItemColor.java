package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class DeviceItemColor implements IItemColor {

    public static DyeColor getColor(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateChildTag(TowerOfGod.MOD_ID);
        if (nbt.contains("Color", Constants.NBT.TAG_INT)) {
            return DyeColor.byId(nbt.getInt("Color"));
        }
        nbt.putInt("Color", DyeColor.WHITE.getId());
        return DyeColor.WHITE;
    }

    @Override
    public int getColor(ItemStack item, int tintIndex) {
        return getColor(item).getColorValue();
    }
}

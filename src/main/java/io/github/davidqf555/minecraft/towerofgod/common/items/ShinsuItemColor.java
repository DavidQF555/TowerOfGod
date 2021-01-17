package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class ShinsuItemColor implements IItemColor {

    @Override
    public int getColor(@Nonnull ItemStack item, int tintIndex) {
        CompoundNBT nbt = item.getChildTag(TowerOfGod.MOD_ID);
        if (nbt != null) {
            return ShinsuQuality.get(nbt.getString("Quality")).getColor();
        }
        return ShinsuQuality.NONE.getColor();
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ItemStackRenderInfo implements IRenderInfo {

    private final Supplier<ItemStack> item;

    public ItemStackRenderInfo(Supplier<ItemStack> item) {
        this.item = item;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color) {
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(item.get(), (int) x, (int) y);
    }
}

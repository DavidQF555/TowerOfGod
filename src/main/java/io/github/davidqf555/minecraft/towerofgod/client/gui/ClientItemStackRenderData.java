package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.common.data.ItemStackRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ClientItemStackRenderData extends ItemStackRenderData implements IClientRenderData {

    public ClientItemStackRenderData(Supplier<ItemStack> item) {
        super(item);
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color) {
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(get(), (int) x, (int) y);
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IRenderable;

public interface IRenderInfo extends IRenderable {

    void render(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color);

    @Override
    default void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        render(matrixStack, 0, 0, 0, 0, 0, 0xFFFFFFFF);
    }
}

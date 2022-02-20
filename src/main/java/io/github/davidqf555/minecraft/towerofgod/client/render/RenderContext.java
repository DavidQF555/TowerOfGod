package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

public class RenderContext {

    private final MatrixStack matrixStack;
    private final float x, y, blitOffset;
    private final int width, height, color;

    public RenderContext(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color) {
        this.matrixStack = matrixStack;
        this.x = x;
        this.y = y;
        this.blitOffset = blitOffset;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getBlitOffset() {
        return blitOffset;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColor() {
        return color;
    }

}
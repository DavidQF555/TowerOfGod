package io.github.davidqf555.minecraft.towerofgod.client.render;

import net.minecraft.client.gui.GuiGraphics;

public class RenderContext {

    private final GuiGraphics graphics;
    private final float x, y, blitOffset;
    private final int width, height, color;

    public RenderContext(GuiGraphics graphics, float x, float y, float blitOffset, int width, int height, int color) {
        this.graphics = graphics;
        this.x = x;
        this.y = y;
        this.blitOffset = blitOffset;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public GuiGraphics getGraphics() {
        return graphics;
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
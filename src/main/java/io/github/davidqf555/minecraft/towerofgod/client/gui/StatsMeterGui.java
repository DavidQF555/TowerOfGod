package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public abstract class StatsMeterGui extends AbstractGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/bars.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int BLIT_WIDTH = 182;
    private final ClientTextureRenderData background;
    private final ClientTextureRenderData bar;
    private final ClientTextureRenderData lines;
    private final int maxDisplay;
    private final int width;
    private final int height;
    private final int textColor;
    private int value;
    private int max;
    private int x;
    private int y;

    public StatsMeterGui(int x, int y, int width, int height, int value, int max, int maxDisplay, ClientTextureRenderData bar, ClientTextureRenderData background, int textColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.value = value;
        this.max = max;
        this.maxDisplay = maxDisplay;
        this.bar = bar;
        this.background = background;
        this.textColor = textColor;
        lines = new ClientTextureRenderData(TEXTURE, 256, 256, 0, 80, 182, 5);
    }

    public void render(MatrixStack matrixStack) {
        background.render(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF);
        double ratio = value * 1.0 / max;
        bar.setBlitWidth((int) (BLIT_WIDTH * ratio));
        bar.render(matrixStack, x, y, getBlitOffset(), (int) (width * ratio), height, 0xFFFFFFFF);
        int startY;
        if (max < maxDisplay) {
            startY = 7 * max / maxDisplay * 5 + 80;
        } else {
            startY = 115;
        }
        lines.setStartY(startY);
        lines.render(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        String text = value + "";
        float textX = x + (width - font.getStringWidth(text)) / 2f;
        float textY = y - 6;
        font.drawString(matrixStack, text, textX + 1, textY, 0xFF000000);
        font.drawString(matrixStack, text, textX - 1, textY, 0xFF000000);
        font.drawString(matrixStack, text, textX, textY + 1, 0xFF000000);
        font.drawString(matrixStack, text, textX, textY - 1, 0xFF000000);
        font.drawString(matrixStack, text, textX, textY, textColor);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static class Shinsu extends StatsMeterGui {

        private static final int MAX_SHINSU = 200;
        private static final int TEXT_COLOR = 0xFF8CF5FF;
        private static final ClientTextureRenderData BACKGROUND = new ClientTextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 10, 182, 5);

        public Shinsu(int x, int y, int width, int height, int value, int max) {
            super(x, y, width, height, value, max, MAX_SHINSU, new ClientTextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 15, 182, 5), BACKGROUND, TEXT_COLOR);
        }
    }

    public static class Baangs extends StatsMeterGui {

        private static final int MAX_BAANGS = 20;
        private static final int TEXT_COLOR = 0xFF8CF5FF;
        private static final ClientTextureRenderData BACKGROUND = new ClientTextureRenderData(TEXTURE, 256, 256, 0, 10, 182, 5);

        public Baangs(int x, int y, int width, int height, int value, int max) {
            super(x, y, width, height, value, max, MAX_BAANGS, new ClientTextureRenderData(TEXTURE, 256, 256, 0, 15, 182, 5), BACKGROUND, TEXT_COLOR);
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class StatsMeterGui implements IIngameOverlay {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/bars.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int BLIT_WIDTH = 182;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 256, 256, 0, 10, 182, 5);
    private static final int TEXT_COLOR = 0xFF8CF5FF;
    private final TextureRenderData bar;
    private final TextureRenderData lines;
    private final int maxDisplay;
    private final int xOffset;
    private int value;
    private int max;

    public StatsMeterGui(int xOffset, int value, int max, int maxDisplay) {
        this.xOffset = xOffset;
        this.value = value;
        this.max = max;
        this.maxDisplay = maxDisplay;
        this.bar = new TextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 15, 182, 5);
        lines = new TextureRenderData(TEXTURE, 256, 256, 0, 80, 182, 5);
    }

    public static boolean shouldRender() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && !client.player.isSpectator() && !client.options.hideGui && !client.player.isCreative() && (ClientReference.SHINSU.getMax() > 0 || ClientReference.BAANGS.getMax() > 0);
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int screenWidth, int screenHeight) {
        if (shouldRender()) {
            int y = screenHeight - 36;
            int x = screenWidth / 2 + xOffset;
            int width = 85;
            int height = 5;
            BACKGROUND.render(new RenderContext(matrixStack, x, y, gui.getBlitOffset(), width, height, 0xFFFFFFFF));
            double ratio = value * 1.0 / max;
            bar.setBlitWidth((int) (BLIT_WIDTH * ratio));
            bar.render(new RenderContext(matrixStack, x, y, gui.getBlitOffset(), (int) (width * ratio), height, 0xFFFFFFFF));
            int startY;
            if (max < maxDisplay) {
                startY = 7 * max / maxDisplay * 5 + 80;
            } else {
                startY = 115;
            }
            lines.setStartY(startY);
            lines.render(new RenderContext(matrixStack, x, y, gui.getBlitOffset(), width, height, 0xFFFFFFFF));
            Font font = Minecraft.getInstance().font;
            String text = value + "";
            float textX = x + (width - font.width(text)) / 2f;
            float textY = y - 6;
            font.draw(matrixStack, text, textX + 1, textY, 0xFF000000);
            font.draw(matrixStack, text, textX - 1, textY, 0xFF000000);
            font.draw(matrixStack, text, textX, textY + 1, 0xFF000000);
            font.draw(matrixStack, text, textX, textY - 1, 0xFF000000);
            font.draw(matrixStack, text, textX, textY, TEXT_COLOR);
        }
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

}

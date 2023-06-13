package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ShinsuMeterGui implements IGuiOverlay {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/bars.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int BLIT_WIDTH = 182;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 256, 256, 0, 10, 182, 5);
    private static final int TEXT_COLOR = 0xFF8CF5FF;
    private final TextureRenderData bar;
    private final TextureRenderData lines;
    private final int maxDisplay;
    private int value, max;

    public ShinsuMeterGui(int maxDisplay) {
        this.maxDisplay = maxDisplay;
        this.bar = new TextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 15, 182, 5);
        lines = new TextureRenderData(TEXTURE, 256, 256, 0, 80, 182, 5);
    }

    public static boolean shouldRender() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && !client.player.isSpectator() && !client.options.hideGui && !client.player.isCreative() && ClientReference.SHINSU.getMax() > 0 && !ClientReference.UNLOCKED.isEmpty();
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        if (shouldRender()) {
            int y = screenHeight / 2 - 45;
            int x = screenWidth - 9;
            int width = 7;
            int height = 90;
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(centerX, centerY, 0);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
            matrixStack.translate(-centerX, -centerY, 0);
            int drawX = x - (height - width) / 2;
            int drawY = y - (width - height) / 2;
            BACKGROUND.render(new RenderContext(graphics, drawX, drawY, 0, height, width, 0xFFFFFFFF));
            double ratio = value * 1.0 / max;
            bar.setBlitWidth((int) (BLIT_WIDTH * ratio));
            bar.render(new RenderContext(graphics, drawX, drawY, 0, (int) (height * ratio), width, 0xFFFFFFFF));
            int startY;
            if (max < maxDisplay) {
                startY = 7 * max / maxDisplay * 5 + 80;
            } else {
                startY = 115;
            }
            lines.setStartY(startY);
            lines.render(new RenderContext(graphics, drawX, drawY, 0, height, width, 0xFFFFFFFF));
            matrixStack.popPose();
            Font font = Minecraft.getInstance().font;
            String text = String.valueOf(value);
            int textX = centerX - font.width(text) / 2;
            int textY = centerY - font.lineHeight / 2;
            graphics.drawString(font, text, textX, textY, TEXT_COLOR, true);
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

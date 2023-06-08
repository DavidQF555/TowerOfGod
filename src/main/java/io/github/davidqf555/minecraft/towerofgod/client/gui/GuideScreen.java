package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GuideScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui." + TowerOfGod.MOD_ID + ".guide");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/guide.png");
    private static final TextureRenderData OUTLINE = new TextureRenderData(TEXTURE, 221, 370, 0, 0, 221, 180);
    private static final TextureRenderData PAGE = new TextureRenderData(TEXTURE, 221, 370, 0, 180, 221, 180);
    private static final TextureRenderData NEXT = new TextureRenderData(TEXTURE, 221, 370, 0, 360, 18, 10);
    private static final TextureRenderData BACK = new TextureRenderData(TEXTURE, 221, 370, 18, 360, 18, 10);
    private static final TextureRenderData ARROW = new TextureRenderData(TEXTURE, 221, 370, 36, 360, 10, 10);
    private static final int BUTTON_WIDTH = 18, BUTTON_HEIGHT = 10, ARROW_WIDTH = 10, ARROW_HEIGHT = 10, DIF = 10;
    private final ShinsuTechnique[] pages;
    private final int xSize, ySize, color;
    private ChangePageButton next, back;
    private int page;

    public GuideScreen(ShinsuTechnique[] pages, int xSize, int ySize, int color) {
        super(TITLE);
        this.pages = pages;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        next = new ChangePageButton(NEXT, 1, x + xSize - BUTTON_WIDTH - 15, y + ySize - BUTTON_HEIGHT - 12);
        back = new ChangePageButton(BACK, -1, x + 15, y + ySize - BUTTON_HEIGHT - 12);
        addRenderableWidget(back);
        addRenderableWidget(next);
        setPage(0);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        PoseStack stack = graphics.pose();
        PAGE.render(new RenderContext(stack, x, y, 0, xSize, ySize, 0xFFFFFFFF));
        OUTLINE.render(new RenderContext(stack, x, y, 0, xSize, ySize, color));
        int centerX = x + xSize / 2;
        int difY = font.lineHeight;
        Component title = pages[page].getText().withStyle(ChatFormatting.BOLD);
        graphics.drawString(font, title, centerX - font.width(title) / 2, y + difY * 2, 0xFF000000);
        pages[page].getIcon().render(new RenderContext(stack, centerX - difY, y + difY * 4, 0, difY * 2, difY * 2, 0xFFFFFFFF));
        List<Direction> combo = pages[page].getCombination();
        int width = combo.size() * ARROW_WIDTH + (combo.size() - 1) * DIF;
        for (int i = 0; i < combo.size(); i++) {
            float arrowX = centerX - width / 2f + (ARROW_HEIGHT + DIF) * i + ARROW_WIDTH / 2f;
            float arrowY = y + difY * 8 + ARROW_HEIGHT / 2f;
            Direction dir = combo.get(i);
            stack.pushPose();
            stack.translate(arrowX, arrowY, 0);
            stack.mulPose(Axis.ZP.rotationDegrees(dir.getAngle() + 180));
            stack.translate(-arrowX, -arrowY, 0);
            ARROW.render(new RenderContext(stack, arrowX - ARROW_WIDTH / 2f, arrowY - ARROW_HEIGHT / 2f, 0, ARROW_WIDTH, ARROW_HEIGHT, color));
            stack.popPose();
        }
        int lines = 0;
        for (IRequirement req : pages[page].getRequirements()) {
            Component text = req.getText();
            lines += renderWrappedText(graphics, text, centerX, y + difY * 10 + lines * font.lineHeight, xSize * 4 / 5, 0xFF000000);
        }
        renderWrappedText(graphics, pages[page].getDescription(), centerX, y + difY * 12 + lines * font.lineHeight, xSize * 4 / 5, 0xFF000000);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    //needs improvement
    private int renderWrappedText(GuiGraphics graphics, Component text, int centerX, int y, int totalWidth, int color) {
        String[] words = text.getString().split(" ");
        int line = 0;
        int current = 0;
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            int width = font.width(word);
            if (width <= totalWidth - current || builder.length() == 0 && width > totalWidth) {
                if (builder.length() != 0) {
                    builder.append(' ');
                }
                builder.append(word);
                current += width;
            } else {
                String string = builder.toString();
                graphics.drawString(font, string, centerX - font.width(string) / 2, y + line * font.lineHeight, color);
                builder = new StringBuilder();
                builder.append(word);
                current = width;
                line++;
            }
        }
        String string = builder.toString();
        graphics.drawString(font, string, centerX - font.width(string) / 2, y + line * font.lineHeight, color);
        return line + 1;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void setPage(int page) {
        this.page = page;
        next.visible = page != pages.length - 1;
        back.visible = page != 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ChangePageButton extends AbstractButton {

        private final IRenderData render;
        private final int change;

        public ChangePageButton(IRenderData render, int change, int x, int y) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.empty());
            this.change = change;
            this.render = render;
        }

        @Override
        public void onPress() {
            setPage(page + change);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                render.render(new RenderContext(graphics.pose(), getX(), getY(), 0, width, height, 0xFFFFFFFF));
            }
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

    }

}

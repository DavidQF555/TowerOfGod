package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class GuideScreen extends Screen {

    private static final Component TITLE = new TranslatableComponent("gui." + TowerOfGod.MOD_ID + ".guide");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/guide.png");
    private static final TextureRenderData OUTLINE = new TextureRenderData(TEXTURE, 221, 370, 0, 0, 221, 180);
    private static final TextureRenderData PAGE = new TextureRenderData(TEXTURE, 221, 370, 0, 180, 221, 180);
    private static final TextureRenderData NEXT = new TextureRenderData(TEXTURE, 221, 370, 0, 360, 18, 10);
    private static final TextureRenderData BACK = new TextureRenderData(TEXTURE, 221, 370, 18, 360, 18, 10);
    private static final int BUTTON_WIDTH = 18, BUTTON_HEIGHT = 10, ARROW_WIDTH = 10, ARROW_HEIGHT = 10, DIF = 10;
    private final ConfiguredShinsuTechniqueType<?, ?>[] pages;
    private final int xSize, ySize, color;
    private ChangePageButton next, back;
    private int page;

    public GuideScreen(ConfiguredShinsuTechniqueType<?, ?>[] pages, int xSize, int ySize, int color) {
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float x = (width - xSize) / 2f;
        float y = (height - ySize) / 2f;
        int z = getBlitOffset();
        PAGE.render(new RenderContext(matrixStack, x, y, z, xSize, ySize, 0xFFFFFFFF));
        OUTLINE.render(new RenderContext(matrixStack, x, y, z, xSize, ySize, color));
        float centerX = x + xSize / 2f;
        int difY = font.lineHeight;
        Component title = pages[page].getConfig().getDisplay().getName().withStyle(ChatFormatting.BOLD);
        font.draw(matrixStack, title, centerX - font.width(title) / 2f, y + difY * 2, 0xFF000000);
        pages[page].getConfig().getDisplay().getIcon().render(new RenderContext(matrixStack, centerX - difY, y + difY * 4, z, difY * 2, difY * 2, 0xFFFFFFFF));
        int lines = 0;
        for (IRequirement req : pages[page].getType().getRequirements()) {
            Component text = req.getText();
            lines += renderWrappedText(matrixStack, text, centerX, y + difY * 10 + lines * font.lineHeight, xSize * 4 / 5, 0xFF000000);
        }
        renderWrappedText(matrixStack, pages[page].getConfig().getDisplay().getDescription(), centerX, y + difY * 12 + lines * font.lineHeight, xSize * 4 / 5, 0xFF000000);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    //needs improvement
    private int renderWrappedText(PoseStack stack, Component text, float centerX, float y, int totalWidth, int color) {
        String[] words = text.getString().split(" ");
        int line = 0;
        int current = 0;
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            int width = font.width(word);
            if (width <= totalWidth - current || builder.isEmpty() && width > totalWidth) {
                if (!builder.isEmpty()) {
                    builder.append(' ');
                }
                builder.append(word);
                current += width;
            } else {
                String string = builder.toString();
                font.draw(stack, string, centerX - font.width(string) / 2f, y + line * font.lineHeight, color);
                builder = new StringBuilder();
                builder.append(word);
                current = width;
                line++;
            }
        }
        String string = builder.toString();
        font.draw(stack, string, centerX - font.width(string) / 2f, y + line * font.lineHeight, color);
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
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, TextComponent.EMPTY);
            this.change = change;
            this.render = render;
        }

        @Override
        public void onPress() {
            setPage(page + change);
        }

        @Override
        public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                render.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

    }

}

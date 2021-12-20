package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ErrorMessages;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.List;

public class GuideScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".guide");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/guide.png");
    private static final TextureRenderData OUTLINE = new TextureRenderData(TEXTURE, 221, 370, 0, 0, 221, 180);
    private static final TextureRenderData PAGE = new TextureRenderData(TEXTURE, 221, 370, 0, 180, 221, 180);
    private static final TextureRenderData NEXT = new TextureRenderData(TEXTURE, 221, 370, 0, 360, 18, 10);
    private static final TextureRenderData BACK = new TextureRenderData(TEXTURE, 221, 370, 18, 360, 18, 10);
    private static final TextureRenderData ARROW = new TextureRenderData(TEXTURE, 221, 370, 36, 360, 10, 10);
    private static final int BUTTON_WIDTH = 18, BUTTON_HEIGHT = 10, ARROW_WIDTH = 10, ARROW_HEIGHT = 10, DIF = 10;
    private final ShinsuTechnique[] pages;
    private final int xSize, ySize, color;
    private int page;

    public GuideScreen(ShinsuTechniqueType type, int xSize, int ySize, int color) {
        super(TITLE);
        pages = Arrays.stream(ShinsuTechnique.values()).filter(tech -> tech.getType() == type).toArray(ShinsuTechnique[]::new);
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        addButton(new ChangePageButton(BACK, -1, x, y + ySize - BUTTON_HEIGHT));
        addButton(new ChangePageButton(NEXT, 1, x + xSize - BUTTON_WIDTH, y + ySize - BUTTON_HEIGHT));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float x = (width - xSize) / 2f;
        float y = (height - ySize) / 2f;
        int z = getBlitOffset();
        PAGE.render(new RenderContext(matrixStack, x, y, z, xSize, ySize, 0xFFFFFFFF));
        OUTLINE.render(new RenderContext(matrixStack, x, y, z, xSize, ySize, color));
        float centerX = x + xSize / 2f;
        int difY = font.FONT_HEIGHT;
        ITextComponent title = pages[page].getText().mergeStyle(TextFormatting.BOLD);
        font.drawText(matrixStack, title, centerX - font.getStringPropertyWidth(title) / 2f, y + difY * 2, 0xFF000000);
        pages[page].getIcon().render(new RenderContext(matrixStack, centerX - difY, y + difY * 4, z, difY * 2, difY * 2, 0xFFFFFFFF));
        List<Direction> combo = pages[page].getCombination();
        int width = combo.size() * ARROW_WIDTH + (combo.size() - 1) * DIF;
        for (int i = 0; i < combo.size(); i++) {
            float arrowX = centerX - width / 2f + (ARROW_HEIGHT + DIF) * i + ARROW_WIDTH / 2f;
            float arrowY = y + difY * 8 + ARROW_HEIGHT / 2f;
            Direction dir = combo.get(i);
            matrixStack.push();
            matrixStack.translate(arrowX, arrowY, 0);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(dir.getAngle() + 180));
            matrixStack.translate(-arrowX, -arrowY, 0);
            ARROW.render(new RenderContext(matrixStack, arrowX - ARROW_WIDTH / 2f, arrowY - ARROW_HEIGHT / 2f, z, ARROW_WIDTH, ARROW_HEIGHT, 0xFF0000FF));
            matrixStack.pop();
        }
        ITextComponent req = ErrorMessages.REQUIRES_LEVEL.apply(pages[page].getType(), pages[page].getLevelRequirement());
        font.drawText(matrixStack, req, centerX - font.getStringPropertyWidth(req) / 2f, y + difY * 10, 0xFF000000);
        ITextComponent desc = pages[page].getDescription();
        renderWrappedText(matrixStack, desc, centerX, y + difY * 12, xSize * 4 / 5, 0xFF000000);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    //needs improvement
    private void renderWrappedText(MatrixStack stack, ITextComponent text, float centerX, float y, int totalWidth, int color) {
        String[] words = text.getString().split(" ");
        int line = 0;
        int current = 0;
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            int width = font.getStringWidth(word);
            if (width <= totalWidth - current || builder.length() == 0 && width > totalWidth) {
                if (builder.length() != 0) {
                    builder.append(' ');
                }
                builder.append(word);
                current += width;
            } else {
                String string = builder.toString();
                font.drawString(stack, string, centerX - font.getStringWidth(string) / 2f, y + line * font.FONT_HEIGHT, color);
                builder = new StringBuilder();
                builder.append(word);
                current = width;
                line++;
            }
        }
        String string = builder.toString();
        font.drawString(stack, string, centerX - font.getStringWidth(string) / 2f, y + line * font.FONT_HEIGHT, color);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode()) {
            closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ChangePageButton extends AbstractButton {

        private final IRenderData render;
        private final int change;

        public ChangePageButton(IRenderData render, int change, int x, int y) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, StringTextComponent.EMPTY);
            this.change = change;
            this.render = render;
        }

        @Override
        public void onPress() {
            page = MathHelper.clamp(page + change, 0, pages.length - 1);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                render.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            }
        }
    }

}

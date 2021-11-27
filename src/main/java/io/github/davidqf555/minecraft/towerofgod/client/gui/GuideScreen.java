package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public class GuideScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".guide");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/guide.png");
    private static final TextureRenderData OUTLINE = new TextureRenderData(TEXTURE, 221, 370, 0, 0, 221, 180);
    private static final TextureRenderData PAGE = new TextureRenderData(TEXTURE, 221, 370, 0, 180, 221, 180);
    private static final TextureRenderData NEXT = new TextureRenderData(TEXTURE, 221, 370, 0, 360, 18, 10);
    private static final TextureRenderData BACK = new TextureRenderData(TEXTURE, 221, 370, 18, 360, 18, 10);
    private static final int BUTTON_WIDTH = 18, BUTTON_HEIGHT = 10;
    private final ShinsuTechnique[] pages;
    private final int xSize, ySize, color;
    private ShinsuCombinationGui combo;
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
        combo = new ShinsuCombinationGui(0, 0);
        updateCombo();
        addButton(new ChangePageButton(BACK, -1, x, y + ySize - BUTTON_HEIGHT));
        addButton(new ChangePageButton(NEXT, 1, x + xSize - BUTTON_WIDTH, y + ySize - BUTTON_HEIGHT));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float x = (width - xSize) / 2f;
        float y = (height - ySize) / 2f;
        int z = getBlitOffset();
        ClientReference.render(PAGE, matrixStack, x, y, z, xSize, ySize, 0xFFFFFFFF);
        ClientReference.render(OUTLINE, matrixStack, x, y, z, xSize, ySize, color);
        ITextComponent title = pages[page].getText();
        float centerX = x + xSize / 2f;
        int difY = font.FONT_HEIGHT;
        combo.renderCombo(matrixStack, centerX - combo.getWidth() / 2f, y + difY * 9);
        ClientReference.render(pages[page].getIcon(), matrixStack, centerX - difY, y + difY * 6, z, difY * 2, difY * 2, 0xFFFFFFFF);
        font.drawText(matrixStack, title, centerX - font.getStringPropertyWidth(title) / 2f, y + difY * 2, 0xFF000000);
        ITextComponent req = new TranslationTextComponent(ShinsuCombinationGui.LEVEL, pages[page].getLevelRequirement(), pages[page].getType().getText());
        font.drawText(matrixStack, req, centerX - font.getStringPropertyWidth(req) / 2f, y + difY * 4, 0xFF000000);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
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

    private void updateCombo() {
        combo.reset();
        for (Direction direction : pages[page].getCombination()) {
            combo.addMarker(direction);
        }
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
            updateCombo();
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                ClientReference.render(render, matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF);
            }
        }
    }

}

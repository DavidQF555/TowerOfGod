package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ChangeFloorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FloorTeleportationTerminalScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".floor_teleportation_terminal");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/floor_teleportation_terminal.png");
    private static final int TEXTURE_WIDTH = 195;
    private static final int TEXTURE_HEIGHT = 146;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 195, 125);
    private static final TextureRenderData BUTTON = new TextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 121, 125, 21, 21);
    private static final int BUTTON_WIDTH = 21;
    private static final int BUTTON_HEIGHT = 21;
    private static final int BUTTON_GAP = 1;
    private static final int TITLE_COLOR = 0xFF404040;
    private final BlockPos teleporter;
    private final Direction direction;
    private final int level;
    private final int xSize;
    private final int ySize;
    private Display display;
    private int x;
    private int y;

    public FloorTeleportationTerminalScreen(int level, BlockPos teleporter, Direction direction) {
        super(TITLE);
        this.teleporter = teleporter;
        this.direction = direction;
        display = null;
        this.level = level;
        xSize = 195;
        ySize = 125;
        x = 0;
        y = 0;
    }

    @Override
    public void init() {
        super.init();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        display = new Display(this, x + (xSize - 121) / 2, y + 20, 121, 21);
        addButton(display);
        int keypadLength = BUTTON_GAP * 2 + BUTTON_WIDTH * 3;
        int keypadX = x + (xSize - keypadLength) / 2;
        int keypadY = (ySize + y + display.y + display.getHeight() - keypadLength) / 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = i * 3 + j;
                int buttonX = keypadX + j * (BUTTON_WIDTH + BUTTON_GAP);
                int buttonY = keypadY + i * (BUTTON_HEIGHT + BUTTON_GAP);
                addButton(new NumberButton(value, buttonX, buttonY));
            }
        }
        int endX = keypadX + keypadLength + BUTTON_GAP;
        int deleteY = keypadY + BUTTON_GAP * 2 + BUTTON_HEIGHT * 2;
        addButton(new DeleteButton(this, endX, deleteY));
        int teleportY = keypadY + BUTTON_GAP + BUTTON_HEIGHT;
        addButton(new TeleportButton(this, endX, teleportY));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        BACKGROUND.render(new RenderContext(matrixStack, x, y, getBlitOffset(), xSize, ySize, 0xFFFFFFFF));
        font.drawText(matrixStack, title, x + (xSize - font.getStringPropertyWidth(title)) / 2f, y + ySize / 20f, TITLE_COLOR);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode()) {
            closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static class Display extends Widget {

        private static final TextureRenderData RENDER = new TextureRenderData(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 125, 121, 21);
        private final FloorTeleportationTerminalScreen screen;
        private final StringBuilder value;
        private boolean valid;

        public Display(FloorTeleportationTerminalScreen screen, int x, int y, int width, int height) {
            super(x, y, width, height, StringTextComponent.EMPTY);
            this.screen = screen;
            value = new StringBuilder();
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            RENDER.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            String value = this.value.toString();
            int textX = x + (width - screen.font.getStringWidth(value)) / 2;
            int textY = y + (height - screen.font.FONT_HEIGHT) / 2;
            screen.font.drawString(matrixStack, value, textX, textY, valid ? 0xFFFFFFFF : 0xFFFF0000);
        }

        private void updateValidity() {
            try {
                int value = Integer.parseInt(this.value.toString());
                valid = value <= screen.level && value >= 1;
            } catch (NumberFormatException exception) {
                valid = false;
            }
        }
    }

    private static class DeleteButton extends AbstractButton {

        private final FloorTeleportationTerminalScreen screen;

        public DeleteButton(FloorTeleportationTerminalScreen screen, int x, int y) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, StringTextComponent.EMPTY);
            this.screen = screen;
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            BUTTON.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            String text = "<";
            int textX = x + (width - screen.font.getStringWidth(text)) / 2;
            int textY = y + (height - screen.font.FONT_HEIGHT) / 2;
            screen.font.drawString(matrixStack, text, textX, textY, 0xFFFFFFFF);
        }

        @Override
        public void onPress() {
            if (screen.display.value.length() > 0) {
                screen.display.value.deleteCharAt(screen.display.value.length() - 1);
                screen.display.updateValidity();
            }
        }
    }

    private static class TeleportButton extends AbstractButton {

        private final FloorTeleportationTerminalScreen screen;

        public TeleportButton(FloorTeleportationTerminalScreen screen, int x, int y) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, StringTextComponent.EMPTY);
            this.screen = screen;
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            BUTTON.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            String text = ">";
            int textX = x + (width - screen.font.getStringWidth(text)) / 2;
            int textY = y + (height - screen.font.FONT_HEIGHT) / 2;
            screen.font.drawString(matrixStack, text, textX, textY, 0xFFFFFFFF);
        }

        @Override
        public void onPress() {
            if (screen.display.valid) {
                int level = Integer.parseInt(screen.display.value.toString());
                TowerOfGod.CHANNEL.sendToServer(new ChangeFloorPacket(level, screen.teleporter, screen.direction));
            }
        }
    }

    private class NumberButton extends AbstractButton {

        private final int value;

        public NumberButton(int value, int x, int y) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, StringTextComponent.EMPTY);
            this.value = value;
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            BUTTON.render(new RenderContext(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
            String value = "" + this.value;
            int textX = x + (width - font.getStringWidth(value)) / 2;
            int textY = y + (height - font.FONT_HEIGHT) / 2;
            font.drawString(matrixStack, value, textX, textY, 0xFFFFFFFF);
        }

        @Override
        public void onPress() {
            display.value.append(value);
            display.updateValidity();
        }
    }
}

package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import com.davidqf.minecraft.towerofgod.common.packets.ChangeEquipsMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuEquipScreen extends Screen {

    private static final int TEXTURE_WIDTH = 195;
    private static final int TEXTURE_HEIGHT = 184;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_equip_screen.png");
    private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".shinsu_equip_screen");
    private static final RenderInfo BACKGROUND = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 195, 166);
    private static final int TITLE_COLOR = 0xFF404040;
    public static Map<ShinsuTechnique, Integer> known = Maps.newEnumMap(ShinsuTechnique.class);
    private final ShinsuSlot[][] slots;
    private final List<ShinsuTechnique> unlocked;
    private final ShinsuSlot[] selected;
    private final ShinsuTechnique[] equipped;
    private Scroller scroller;
    private final int xSize;
    private final int ySize;
    private int x;
    private int y;
    private int topRow;

    public ShinsuEquipScreen(int xSize, int ySize) {
        super(TITLE);
        x = 0;
        y = 0;
        this.xSize = xSize;
        this.ySize = ySize;
        slots = new ShinsuSlot[3][9];
        unlocked = new ArrayList<>();
        for (ShinsuTechnique technique : known.keySet()) {
            if (technique.isObtainable() && known.get(technique) > 0) {
                unlocked.add(technique);
            }
        }
        selected = new ShinsuSlot[4];
        equipped = new ShinsuTechnique[4];
        System.arraycopy(ShinsuSkillWheelGui.equipped, 0, equipped, 0, Math.min(equipped.length, ShinsuSkillWheelGui.equipped.length));
        scroller = null;
        topRow = 0;
    }

    @Override
    public void init() {
        super.init();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        addSlots();
        for (ShinsuSlot slot : selected) {
            unlocked.remove(slot.technique);
        }
        scroller = new Scroller(this, x + 175, y + 84, 12, 15, y + 84, y + 136);
        addButton(scroller);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scroller.color = Scroller.COLOR;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        updateSlots();
        BACKGROUND.render(matrixStack, x, y, getBlitOffset(), xSize, ySize, 0xFFFFFFFF);
        font.func_238422_b_(matrixStack, title, x + (xSize - font.func_238414_a_(title)) / 2f, y + ySize / 20f, TITLE_COLOR);
        ShinsuSlot hovered = null;
        for (Widget button : buttons) {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
            if (hovered == null && button instanceof ShinsuSlot && ((ShinsuSlot) button).technique != null && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight()) {
                hovered = (ShinsuSlot) button;
            }
        }
        if (hovered != null) {
            hovered.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    private void addSlots() {
        int width = 16;
        int height = 16;
        for (int i = 0; i < selected.length; i++) {
            ShinsuSlot slot = new ShinsuSlot(this, x + 16 + 49 * i, y + 35, width, height, i < equipped.length ? equipped[i] : null);
            selected[i] = slot;
            addButton(slot);
        }
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                ShinsuSlot slot = new ShinsuSlot(this, x + 8 + 18 * j, y + 84 + 18 * i, width, height, null);
                slots[i][j] = slot;
                addButton(slot);
            }
        }
    }

    private void updateSlots() {
        int i = 0;
        int j = 0;
        int start = topRow * slots[0].length;
        for (int t = start; t < start + slots.length * slots[0].length; t++) {
            slots[i][j].technique = t < unlocked.size() ? unlocked.get(t) : null;
            if (j >= slots[i].length - 1) {
                i++;
                j = 0;
            } else {
                j++;
            }
        }
    }

    private int totalRows() {
        int amt = unlocked.size() / slots[0].length;
        if (unlocked.size() % slots[0].length > 0) {
            amt++;
        }
        return amt;
    }

    private static class ShinsuSlot extends AbstractButton {

        private static final String LEVEL_TRANSLATION_KEY = "gui." + TowerOfGod.MOD_ID + ".level";
        private final ShinsuEquipScreen screen;
        private ShinsuTechnique technique;

        private ShinsuSlot(ShinsuEquipScreen screen, int x, int y, int width, int height, @Nullable ShinsuTechnique technique) {
            super(x, y, width, height, StringTextComponent.EMPTY);
            this.screen = screen;
            this.technique = technique;
        }

        @Override
        public void onPress() {
            if (isSelected()) {
                for (int i = 0; i < screen.selected.length; i++) {
                    if (equals(screen.selected[i])) {
                        screen.equipped[i] = null;
                        break;
                    }
                }
                screen.unlocked.add(technique);
                technique = null;
            } else {
                for (int i = 0; i < screen.selected.length; i++) {
                    ShinsuSlot slot = screen.selected[i];
                    if (slot.technique == null) {
                        screen.equipped[i] = technique;
                        screen.selected[i].technique = technique;
                        slot.technique = technique;
                        screen.unlocked.remove(technique);
                        break;
                    }
                }
            }
            ChangeEquipsMessage.INSTANCE.sendToServer(new ChangeEquipsMessage(screen.equipped));
        }

        private boolean isSelected() {
            for (ShinsuSlot slot : screen.selected) {
                if (equals(slot)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            if (technique != null) {
                technique.getIcon().render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }
        }

        private void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
            IFormattableTextComponent name = technique.getText().copyRaw().mergeStyle(TextFormatting.BOLD);
            TranslationTextComponent level = new TranslationTextComponent(LEVEL_TRANSLATION_KEY, known.getOrDefault(technique, 0));
            List<ITextComponent> tooltip = new ArrayList<>(Arrays.asList(name, level));
            int maxLength = 0;
            for (ITextComponent t : tooltip) {
                int length = screen.font.func_238414_a_(t);
                if (length > maxLength) {
                    maxLength = length;
                }
            }
            GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX, mouseY, screen.width, screen.height, maxLength, screen.font);
        }
    }

    private static class Scroller extends Widget {

        private static final int COLOR = 0xFFFFFFFF;
        private static final int DRAG_COLOR = 0xFFAAAAAA;
        private static final RenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 166, 12, 15);
        private final int minY;
        private final int maxY;
        private final ShinsuEquipScreen screen;
        private int color;

        private Scroller(ShinsuEquipScreen screen, int x, int y, int width, int height, int minY, int maxY) {
            super(x, y, width, height, StringTextComponent.EMPTY);
            this.screen = screen;
            this.minY = minY;
            this.maxY = maxY;
            color = COLOR;
        }

        @Override
        protected boolean clicked(double mouseX, double mouseY) {
            boolean clicked = super.clicked(mouseX, mouseY);
            if (clicked) {
                color = DRAG_COLOR;
            }
            return clicked;
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            int max = maxY - height;
            int newY = y + (int) deltaY;
            int scrollableDist = maxY - height - minY;
            screen.topRow += (screen.totalRows() - screen.slots.length + 1) * (newY - y) / scrollableDist;
            y = Math.min(max, Math.max(minY, newY));
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            RENDER.render(matrixStack, x, y, 0, width, height, color);
        }
    }

    public static class OpenButton extends Button {

        private static final RenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 166, 20, 18);
        private static final RenderInfo HOVERED = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 166, 20, 18);
        private final Screen screen;

        public OpenButton(Screen screen, int x, int y, int width, int height) {
            super(x, y, width, height, StringTextComponent.EMPTY, press -> Minecraft.getInstance().displayGuiScreen(new ShinsuEquipScreen(195, 166)));
            this.screen = screen;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (screen instanceof CreativeScreen) {
                int index = ((CreativeScreen) screen).getSelectedTabIndex();
                visible = index == ItemGroup.INVENTORY.getIndex();
            }
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            if (isHovered()) {
                HOVERED.render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            } else {
                RENDER.render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }
            ShinsuIcons.SHINSU.render(matrixStack, x + 2, y + 2, screen.getBlitOffset(), width - 4, height - 4, 0xFFFFFFFF);
        }
    }
}

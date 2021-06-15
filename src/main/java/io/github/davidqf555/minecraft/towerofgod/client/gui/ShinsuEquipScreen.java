package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderInfo;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ChangeEquipsMessage;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.TechniqueSettings;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuEquipScreen extends Screen {

    private static final int TEXTURE_WIDTH = 195;
    private static final int TEXTURE_HEIGHT = 184;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_equip_screen.png");
    private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".shinsu_equip_screen");
    private static final RenderInfo BACKGROUND = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 195, 166);
    private static final int TITLE_COLOR = 0xFF404040;
    private static final ITextComponent TECHNIQUES = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".techniques");
    private final Slot[][] slots;
    private final Map<ShinsuTechnique, Set<String>> available;
    private final Slot[] selected;
    private final int xSize;
    private final int ySize;
    private int selectedSlot;
    private Scroller scroller;
    private int x;
    private int y;
    private int topRow;

    public ShinsuEquipScreen(int xSize, int ySize) {
        super(TITLE);
        x = 0;
        y = 0;
        this.xSize = xSize;
        this.ySize = ySize;
        slots = new Slot[3][9];
        available = new EnumMap<>(ShinsuTechnique.class);
        for (ShinsuTechnique technique : ClientReference.known.keySet()) {
            if (technique.isObtainable() && ClientReference.known.get(technique) > 0) {
                available.put(technique, new HashSet<>(technique.getSettings().getOptions()));
            }
        }
        selected = new Slot[4];
        scroller = null;
        selectedSlot = -1;
        topRow = 0;
    }

    private void updateEquips() {
        ShinsuTechnique[] equipped = Arrays.stream(selected).map(slot -> slot.technique).toArray(ShinsuTechnique[]::new);
        String[] settings = Arrays.stream(selected).map(slot -> slot.settings).toArray(String[]::new);
        TowerOfGod.CHANNEL.sendToServer(new ChangeEquipsMessage(equipped, settings));
        ClientReference.equipped = equipped;
        ClientReference.settings = settings;
    }

    @Override
    public void init() {
        super.init();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        addSlots();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i].technique != null) {
                selected[i].settings = i >= ClientReference.settings.length || ClientReference.settings[i] == null ? selected[i].technique.getSettings().getDefault() : ClientReference.settings[i];
                available.get(selected[i].technique).remove(selected[i].settings);
            }
        }
        scroller = new Scroller(this, x + 175, y + 84, 12, 15, y + 84, y + 136);
        addButton(scroller);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
        if (minecraft.gameSettings.keyBindInventory.isActiveAndMatches(input)) {
            closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        renderBackground(matrixStack);
        BACKGROUND.render(matrixStack, x, y, getBlitOffset(), xSize, ySize, 0xFFFFFFFF);
        font.drawText(matrixStack, title, x + (xSize - font.getStringPropertyWidth(title)) / 2f, y + ySize / 20f, TITLE_COLOR);
        ITextComponent text;
        if (selectedSlot == -1) {
            text = TECHNIQUES;
        } else {
            text = selected[selectedSlot].technique.getSettings().getTitle();
        }
        font.drawText(matrixStack, text, x + 8, y + 83 - font.FONT_HEIGHT, TITLE_COLOR);
        Slot hovered = null;
        for (Widget button : buttons) {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
            if (hovered == null && button instanceof Slot && (((Slot) button).technique != null || ((Slot) button).settings != null) && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight()) {
                hovered = (Slot) button;
            }
        }
        if (hovered != null) {
            hovered.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addSlots() {
        int width = 16;
        int height = 16;
        for (int i = 0; i < selected.length; i++) {
            Slot slot = new Slot(i, this, x + 16 + 49 * i, y + 35, width, height, i < ClientReference.equipped.length ? ClientReference.equipped[i] : null);
            selected[i] = slot;
            addButton(new RemoveButton(slot));
        }
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                Slot slot = new Slot(-1, this, x + 8 + 18 * j, y + 84 + 18 * i, width, height, null);
                slots[i][j] = slot;
            }
        }
    }

    private void updateSlots() {
        int i = 0;
        int j = 0;
        List<ShinsuTechnique> list = available.keySet().stream().filter(technique -> !available.get(technique).isEmpty()).collect(Collectors.toList());
        int start = topRow * slots[0].length;
        for (int t = start; t < start + slots.length * slots[0].length; t++) {
            if (selectedSlot == -1) {
                slots[i][j].technique = t < list.size() ? list.get(t) : null;
                slots[i][j].settings = null;
            } else {
                List<String> options = new ArrayList<>(available.get(selected[selectedSlot].technique));
                options.add(0, selected[selectedSlot].settings);
                slots[i][j].settings = t < options.size() ? options.get(t) : null;
                slots[i][j].technique = null;
            }
            if (j >= slots[i].length - 1) {
                i++;
                j = 0;
            } else {
                j++;
            }
        }
    }

    private int totalRows() {
        int amt = available.size() / slots[0].length;
        if (available.size() % slots[0].length > 0) {
            amt++;
        }
        return amt;
    }

    private static class Slot extends AbstractButton {

        private static final String LEVEL_TRANSLATION_KEY = "gui." + TowerOfGod.MOD_ID + ".level";
        private static final String SETTINGS_TRANSLATION_KEY = "gui." + TowerOfGod.MOD_ID + ".settings";
        private final int index;
        private final ShinsuEquipScreen screen;
        private ShinsuTechnique technique;
        private String settings;

        private Slot(int index, ShinsuEquipScreen screen, int x, int y, int width, int height, @Nullable ShinsuTechnique technique) {
            super(x, y, width, height, StringTextComponent.EMPTY);
            this.index = index;
            this.screen = screen;
            this.technique = technique;
            settings = null;
            screen.addButton(this);
        }

        @Override
        public void onPress() {
            if (index == -1) {
                if (settings != null) {
                    Set<String> available = screen.available.get(screen.selected[screen.selectedSlot].technique);
                    available.add(screen.selected[screen.selectedSlot].settings);
                    available.remove(settings);
                    screen.selected[screen.selectedSlot].settings = settings;
                    screen.selectedSlot = -1;
                    screen.updateEquips();
                } else if (technique != null) {
                    for (int i = 0; i < screen.selected.length; i++) {
                        if (screen.selected[i].technique == null) {
                            screen.selected[i].technique = technique;
                            List<String> available = new ArrayList<>(screen.available.get(technique));
                            screen.selected[i].settings = available.get(0);
                            screen.available.get(technique).remove(screen.selected[i].settings);
                            screen.updateEquips();
                            break;
                        }
                    }
                }
            } else if (!screen.available.get(technique).isEmpty()) {
                screen.selectedSlot = index;
            }
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (index == -1 && settings != null) {
                screen.selected[screen.selectedSlot].technique.getSettings().getIcon(settings).render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            } else if (technique != null) {
                technique.getIcon().render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }
        }

        private void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
            List<ITextComponent> tooltip = new ArrayList<>();
            if (technique != null) {
                tooltip.add(technique.getText().copyRaw().mergeStyle(TextFormatting.BOLD));
                if (settings != null && !settings.isEmpty()) {
                    TechniqueSettings s = technique.getSettings();
                    tooltip.add(new TranslationTextComponent(SETTINGS_TRANSLATION_KEY, s.getTitle(), s.getText(settings)));
                }
                tooltip.add(new TranslationTextComponent(LEVEL_TRANSLATION_KEY, ClientReference.known.getOrDefault(technique, 0)));
            } else if (settings != null) {
                tooltip.add(screen.selected[screen.selectedSlot].technique.getSettings().getText(settings));
            } else {
                return;
            }
            int maxLength = 0;
            for (ITextComponent t : tooltip) {
                int length = screen.font.getStringPropertyWidth(t);
                if (length > maxLength) {
                    maxLength = length;
                }
            }
            GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX, mouseY, screen.width, screen.height, maxLength, screen.font);
        }
    }

    private static class RemoveButton extends AbstractButton {

        private static final RenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 52, 166, 5, 5);
        private static final int WIDTH = 8;
        private static final int HEIGHT = 8;
        private final Slot slot;

        private RemoveButton(Slot slot) {
            super(slot.x + slot.getWidth() - WIDTH / 2, slot.y + slot.getHeight() - HEIGHT / 2, WIDTH, HEIGHT, StringTextComponent.EMPTY);
            this.slot = slot;
        }

        @Override
        public void onPress() {
            Set<String> settings = slot.screen.available.get(slot.technique);
            settings.add(slot.settings);
            slot.technique = null;
            slot.settings = null;
            if (slot.screen.selectedSlot == slot.index) {
                slot.screen.selectedSlot = -1;
            }
            slot.screen.updateEquips();
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            visible = slot.screen.selected[slot.index].technique != null;
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            RENDER.render(matrixStack, x, y, slot.screen.getBlitOffset(), width, height, 0xFFFFFFFF);
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
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
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
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            if (isHovered()) {
                HOVERED.render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            } else {
                RENDER.render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }
            ShinsuIcons.SHINSU.render(matrixStack, x + 2, y + 2, screen.getBlitOffset(), width - 4, height - 4, 0xFFFFFFFF);
        }
    }
}

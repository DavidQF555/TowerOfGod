package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ChangeEquipsPacket;
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
import net.minecraft.util.math.MathHelper;
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
    private static final int TEXTURE_HEIGHT = 190;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_equip_screen.png");
    private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".shinsu_equip_screen");
    private static final IRenderInfo BACKGROUND = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 195, 166);
    private static final String LEVEL_TRANSLATION_KEY = "gui." + TowerOfGod.MOD_ID + ".level";
    private static final String SETTINGS_TRANSLATION_KEY = "gui." + TowerOfGod.MOD_ID + ".settings";
    private static final int TITLE_COLOR = 0xFF404040;
    private static final ITextComponent TECHNIQUES = new TranslationTextComponent("gui." + TowerOfGod.MOD_ID + ".techniques");
    private final Slot[][] slots;
    private final Map<ShinsuTechnique, Set<String>> available;
    private final List<SelectedSlot> selected;
    private final int xSize, ySize;
    private SelectedSlot selectedSlot;
    private Scroller scroller;
    private int x, y, topRow, max;

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
        selected = new ArrayList<>();
        scroller = null;
        selectedSlot = null;
        topRow = 0;
        max = 0;
    }

    private void updateEquips() {
        List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
        selected.forEach(slot -> equipped.add(Pair.of(slot.technique, slot.settings)));
        TowerOfGod.CHANNEL.sendToServer(new ChangeEquipsPacket(equipped));
        ClientReference.equipped = equipped;
    }

    @Override
    public void init() {
        super.init();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        max = xSize / SelectedSlot.WIDTH;
        addSlots();
        updateSlots();
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
        renderBackground(matrixStack);
        BACKGROUND.render(matrixStack, x, y, getBlitOffset(), xSize, ySize, 0xFFFFFFFF);
        font.drawText(matrixStack, title, x + (xSize - font.getStringPropertyWidth(title)) / 2f, y + ySize / 20f, TITLE_COLOR);
        ITextComponent text;
        if (selectedSlot == null) {
            text = TECHNIQUES;
        } else {
            text = selectedSlot.technique.getSettings().getTitle();
        }
        font.drawText(matrixStack, text, x + 8, y + 83 - font.FONT_HEIGHT, TITLE_COLOR);
        Widget hovered = null;
        for (Widget button : buttons) {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
            if (hovered == null && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight()) {
                hovered = button;
            }
        }
        if (hovered != null) {
            hovered.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addSlots() {
        ClientReference.equipped.forEach(pair -> {
            SelectedSlot slot = new SelectedSlot(this, pair.getFirst(), pair.getSecond());
            selected.add(slot);
            available.get(slot.technique).remove(slot.settings);
        });
        reformatSelected();
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                Slot slot = new Slot(this, x + 8 + 18 * j, y + 84 + 18 * i, null, null);
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
            if (selectedSlot == null) {
                slots[i][j].technique = t < list.size() ? list.get(t) : null;
                slots[i][j].settings = null;
            } else {
                List<String> options = new ArrayList<>(available.get(selectedSlot.technique));
                options.add(0, selectedSlot.settings);
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

    private void reformatSelected() {
        int size = selected.size();
        if (size > 0) {
            double dif = xSize * 1.0 / size - SelectedSlot.WIDTH;
            int startX = x + (int) dif / 2;
            for (int i = 0; i < size; i++) {
                SelectedSlot slot = selected.get(i);
                slot.x = startX + (int) ((dif + SelectedSlot.WIDTH) * i);
                slot.remove.updatePosition();
            }
        }
    }

    private int totalRows() {
        return MathHelper.ceil(available.size() * 1.0 / slots[0].length);
    }

    private static class SelectedSlot extends AbstractButton {

        private static final IRenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 52, 166, 24, 24);
        private static final int WIDTH = 24;
        private static final int HEIGHT = 24;
        private static final IRenderInfo REMOVE_RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 184, 5, 5);
        private static final int REMOVE_WIDTH = 8;
        private static final int REMOVE_HEIGHT = 8;
        private final ShinsuEquipScreen screen;
        private final ShinsuTechnique technique;
        private final RemoveButton remove;
        private String settings;

        public SelectedSlot(ShinsuEquipScreen screen, ShinsuTechnique technique, String settings) {
            super(0, screen.y + 20, WIDTH, HEIGHT, StringTextComponent.EMPTY);
            this.remove = new RemoveButton();
            this.screen = screen;
            this.technique = technique;
            this.settings = settings;
            screen.addButton(this);
            screen.addButton(remove);
        }

        @Override
        public void onPress() {
            if (!screen.available.get(technique).isEmpty()) {
                screen.selectedSlot = this;
                screen.updateSlots();
            }
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            RENDER.render(matrixStack, x, y, getBlitOffset(), width, height, 0xFFFFFFFF);
            int dif = MathHelper.ceil(HEIGHT / 6.0);
            ClientReference.getShinsuIcon(technique.getIcon()).render(matrixStack, x + dif, y + dif, getBlitOffset(), width - dif * 2, height - dif * 2, 0xFFFFFFFF);
        }

        private void remove() {
            Set<String> settings = screen.available.get(technique);
            settings.add(this.settings);
            screen.selected.remove(this);
            screen.buttons.remove(this);
            screen.children.remove(this);
            screen.buttons.remove(remove);
            screen.children.remove(remove);
            if (equals(screen.selectedSlot)) {
                screen.selectedSlot = null;
            }
            screen.updateSlots();
            screen.reformatSelected();
            screen.updateEquips();
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            List<ITextComponent> tooltip = new ArrayList<>();
            if (technique != null) {
                tooltip.add(technique.getText().copyRaw().mergeStyle(TextFormatting.BOLD));
                if (!settings.isEmpty()) {
                    TechniqueSettings s = technique.getSettings();
                    tooltip.add(new TranslationTextComponent(SETTINGS_TRANSLATION_KEY, s.getTitle(), s.getText(settings)));
                }
                tooltip.add(new TranslationTextComponent(LEVEL_TRANSLATION_KEY, ClientReference.known.getOrDefault(technique, 0)));
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

        private class RemoveButton extends AbstractButton {

            private RemoveButton() {
                super(0, 0, REMOVE_WIDTH, REMOVE_HEIGHT, StringTextComponent.EMPTY);
            }

            @Override
            public void onPress() {
                remove();
            }

            @Override
            public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                REMOVE_RENDER.render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }

            private void updatePosition() {
                x = SelectedSlot.this.x + SelectedSlot.this.width - width / 2;
                y = SelectedSlot.this.y + SelectedSlot.this.height - height / 2;
            }
        }
    }

    private static class Slot extends AbstractButton {

        private static final int WIDTH = 16;
        private static final int HEIGHT = 16;
        private final ShinsuEquipScreen screen;
        private ShinsuTechnique technique;
        private String settings;

        private Slot(ShinsuEquipScreen screen, int x, int y, @Nullable ShinsuTechnique technique, @Nullable String settings) {
            super(x, y, WIDTH, HEIGHT, StringTextComponent.EMPTY);
            this.screen = screen;
            this.technique = technique;
            this.settings = settings;
            screen.addButton(this);
        }

        @Override
        public void onPress() {
            if (settings != null) {
                Set<String> available = screen.available.get(screen.selectedSlot.technique);
                available.add(screen.selectedSlot.settings);
                available.remove(settings);
                screen.selectedSlot.settings = settings;
                screen.selectedSlot = null;
                screen.updateSlots();
                screen.updateEquips();
            } else if (technique != null && screen.selected.size() < screen.max) {
                List<String> available = new ArrayList<>(screen.available.get(technique));
                SelectedSlot slot = new SelectedSlot(screen, technique, available.get(0));
                screen.selected.add(slot);
                screen.available.get(technique).remove(slot.settings);
                screen.updateSlots();
                screen.reformatSelected();
                screen.updateEquips();
            }
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (settings != null) {
                ClientReference.getShinsuIcon(screen.selectedSlot.technique.getSettings().getIcon(settings)).render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            } else if (technique != null) {
                ClientReference.getShinsuIcon(technique.getIcon()).render(matrixStack, x, y, screen.getBlitOffset(), width, height, 0xFFFFFFFF);
            }
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            List<ITextComponent> tooltip = new ArrayList<>();
            if (technique != null) {
                tooltip.add(technique.getText().copyRaw().mergeStyle(TextFormatting.BOLD));
                if (settings != null && !settings.isEmpty()) {
                    TechniqueSettings s = technique.getSettings();
                    tooltip.add(new TranslationTextComponent(SETTINGS_TRANSLATION_KEY, s.getTitle(), s.getText(settings)));
                }
                tooltip.add(new TranslationTextComponent(LEVEL_TRANSLATION_KEY, ClientReference.known.getOrDefault(technique, 0)));
            } else if (settings != null) {
                tooltip.add(screen.selectedSlot.technique.getSettings().getText(settings));
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

    private static class Scroller extends Widget {

        private static final int COLOR = 0xFFFFFFFF;
        private static final int DRAG_COLOR = 0xFFAAAAAA;
        private static final IRenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 166, 12, 15);
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
            screen.updateSlots();
        }

        @Override
        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            RENDER.render(matrixStack, x, y, 0, width, height, color);
        }
    }

    public static class OpenButton extends Button {

        private static final IRenderInfo RENDER = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 166, 20, 18);
        private static final IRenderInfo HOVERED = new RenderInfo(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 166, 20, 18);
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
            ClientReference.getShinsuIcon("shinsu").render(matrixStack, x + 2, y + 2, screen.getBlitOffset(), width - 4, height - 4, 0xFFFFFFFF);
        }
    }
}

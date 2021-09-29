package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientCanCastPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientCooldownsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.TechniqueSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class ShinsuTechniqueBarGui extends AbstractGui implements IRenderable {

    private static final float RESISTIVITY = 10;
    private final List<Slot> slots;
    private final int centerX;
    private final int centerY;
    private float centerYaw;
    private int selected;
    private boolean locked;

    public ShinsuTechniqueBarGui(int x, int y, float centerYaw, List<Pair<ShinsuTechnique, String>> equipped) {
        slots = equipped.stream().map(pair -> new Slot(pair.getFirst(), pair.getSecond())).collect(Collectors.toList());
        selected = (slots.size() - 1) / 2;
        this.centerYaw = centerYaw;
        centerX = x;
        centerY = y;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int y = centerY - Slot.HEIGHT / 2;
        if (locked) {
            Slot slot = slots.get(selected);
            slot.x = centerX - Slot.WIDTH / 2;
            slot.y = y;
            slot.render(matrixStack, mouseX, mouseY, partialTicks);
        } else {
            int startX = centerX - slots.size() * Slot.WIDTH / 2;
            for (int i = 0; i < slots.size(); i++) {
                Slot slot = slots.get(i);
                slot.x = startX + i * Slot.WIDTH;
                slot.y = y;
                slot.selected = i == selected;
                slot.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
        slots.get(selected).renderName(matrixStack);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Pair<ShinsuTechnique, String> getSelected() {
        Slot slot = slots.get(selected);
        return Pair.of(slot.technique, slot.settings);
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (locked) {
            centerYaw = client.player.rotationYawHead - (selected - (slots.size() - 1) / 2f) * RESISTIVITY;
        } else {
            float dYaw = MathHelper.wrapDegrees(client.player.rotationYawHead - centerYaw);
            float maxChange = slots.size() * RESISTIVITY / 2;
            if (dYaw > maxChange) {
                centerYaw += dYaw - maxChange;
                selected = slots.size() - 1;
            } else if (-dYaw > maxChange) {
                centerYaw += dYaw + maxChange;
                selected = 0;
            } else {
                selected = (int) ((slots.size() - 1) / 2f + dYaw / RESISTIVITY);
            }
        }
        TowerOfGod.CHANNEL.sendToServer(new UpdateClientCooldownsPacket());
        TowerOfGod.CHANNEL.sendToServer(new UpdateClientCanCastPacket());
    }

    private static class Slot extends AbstractGui implements IRenderable {

        private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_bar_gui.png");
        private static final IRenderInfo RENDER = new RenderInfo(TEXTURE, 15, 16, 0, 0, 15, 15);
        private static final IRenderInfo COOLDOWN = new RenderInfo(TEXTURE, 15, 16, 15, 0, 1, 1);
        private static final String SETTINGS_KEY = "gui." + TowerOfGod.MOD_ID + ".settings";
        private static final int SELECTED_COLOR = 0xFFFFFFFF;
        private static final int UNSELECTED_COLOR = 0x55FFFFFF;
        private static final int CAN_CAST_COLOR = 0xFF6DC0FF;
        private static final int CANNOT_CAST_COLOR = 0xFFFF4444;
        private static final int COOLDOWN_COLOR = 0x77FFFFFF;
        private static final int WIDTH = 32;
        private static final int HEIGHT = 32;
        private static final int ICON_DIF_WIDTH = MathHelper.ceil(WIDTH / 15.0);
        private static final int ICON_DIF_HEIGHT = MathHelper.ceil(HEIGHT / 15.0);
        private final ShinsuTechnique technique;
        private final String settings;
        private int x, y;
        private boolean selected;

        private Slot(ShinsuTechnique technique, String settings) {
            this.technique = technique;
            this.settings = settings;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            boolean canCast = canCast();
            RENDER.render(matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, ColorHelper.PackedColor.blendColors(selected ? SELECTED_COLOR : UNSELECTED_COLOR, canCast ? CAN_CAST_COLOR : CANNOT_CAST_COLOR));
            technique.getIcon().render(matrixStack, x + ICON_DIF_WIDTH, y + ICON_DIF_HEIGHT, getBlitOffset(), WIDTH - ICON_DIF_WIDTH * 2, HEIGHT - ICON_DIF_HEIGHT * 2, selected ? SELECTED_COLOR : UNSELECTED_COLOR);
            int cooldown = ClientReference.cooldowns.getOrDefault(technique, 0);
            if (cooldown > 0) {
                float percent = ClientReference.initialCooldowns.containsKey(technique) ? cooldown * 1f / ClientReference.initialCooldowns.get(technique) : 1;
                int height = (int) (HEIGHT * percent + 0.5f);
                COOLDOWN.render(matrixStack, x, y + HEIGHT - height, getBlitOffset(), WIDTH, height, ColorHelper.PackedColor.blendColors(COOLDOWN_COLOR, selected ? SELECTED_COLOR : UNSELECTED_COLOR));
            }
        }

        private void renderName(MatrixStack matrixStack) {
            int color = canCast() ? CAN_CAST_COLOR : CANNOT_CAST_COLOR;
            Minecraft client = Minecraft.getInstance();
            int x = this.x + WIDTH / 2;
            ITextComponent title = technique.getText();
            client.fontRenderer.drawText(matrixStack, title, x - client.fontRenderer.getStringPropertyWidth(title) / 2f, y + HEIGHT + 1, color);
            if (!settings.isEmpty()) {
                TechniqueSettings settings = technique.getSettings();
                TranslationTextComponent text = new TranslationTextComponent(SETTINGS_KEY, settings.getTitle(), settings.getText(this.settings));
                client.fontRenderer.drawText(matrixStack, text, x - client.fontRenderer.getStringPropertyWidth(text) / 2f, y + HEIGHT + client.fontRenderer.FONT_HEIGHT + 2, color);
            }
        }

        private boolean canCast() {
            return ClientReference.canCast.containsKey(technique) && ClientReference.canCast.get(technique).contains(settings);
        }
    }
}

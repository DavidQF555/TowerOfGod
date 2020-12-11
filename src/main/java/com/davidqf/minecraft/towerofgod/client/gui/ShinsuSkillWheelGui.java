package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import com.davidqf.minecraft.towerofgod.client.util.IPlayerShinsuEquips;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShinsuSkillWheelGui extends AbstractGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/wheel.png");
    private static final int RADIUS = 100;
    private static final double MIN_MOVEMENT = 1;
    private final Part[] parts;
    private Part selected;
    private final IShinsuStats stats;
    private float yaw, prevYaw, pitch, prevPitch;
    private boolean locked;

    public ShinsuSkillWheelGui() {
        parts = new Part[(int) (360 / Part.ANGLE)];
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ShinsuTechniques[] equips = IPlayerShinsuEquips.get(player).getEquipped();
        float angle = Part.ANGLE / 2;
        for (int i = 0; i < parts.length; i++) {
            parts[i] = new Part(this, i < equips.length ? equips[i] : null, angle);
            angle += Part.ANGLE;
        }
        stats = IShinsuStats.get(player);
        selected = null;
        yaw = 0;
        prevYaw = 0;
        pitch = 0;
        prevPitch = 0;
        locked = false;
    }

    @Nullable
    public ShinsuTechniques getSelected() {
        return selected == null ? null : selected.technique;
    }

    public void render(MatrixStack matrixStack) {
        Minecraft client = Minecraft.getInstance();
        client.getTextureManager().bindTexture(TEXTURE);
        MainWindow window = client.getMainWindow();
        int cenX = window.getScaledWidth() / 2;
        int cenY = window.getScaledHeight() / 2;
        if (locked && selected != null) {
            selected.renderName(matrixStack, cenX, cenY);
        } else {
            float dYaw = yaw - prevYaw;
            float dPitch = pitch - prevPitch;
            double magSq = dYaw * dYaw + dPitch * dPitch;
            if (magSq >= MIN_MOVEMENT * MIN_MOVEMENT) {
                double angle = Math.atan(dPitch / dYaw) * 180 / Math.PI;
                if (dYaw < 0) {
                    angle += 180;
                } else if (angle < 0) {
                    angle += 360;
                }
                for (Part part : parts) {
                    if (part.angle >= angle || (angle + Part.ANGLE > 360 && part.angle >= angle - 360)) {
                        selected = part;
                        break;
                    }
                }
            }
            for (Part part : parts) {
                part.render(matrixStack);
            }
            for (Part part : parts) {
                double angle = part.angle - Part.ANGLE / 2;
                int xOff = (int) (Math.cos(angle * Math.PI / 180) * RADIUS / 2);
                int yOff = -(int) (Math.sin(angle * Math.PI / 180) * RADIUS / 2);
                part.renderName(matrixStack, cenX + xOff, cenY + yOff);
            }
        }
    }

    public void tick() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        prevYaw = yaw;
        yaw = player.rotationYawHead;
        prevPitch = pitch;
        pitch = player.rotationPitch;
    }

    public void lock() {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }

    private static class Part extends AbstractGui {

        private static final RenderInfo RENDER = new RenderInfo(new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/wheel.png"), 64, 64, 0, 0, 64, 64);
        private static final float ANGLE = 90;
        private static final int COLOR = 0x22FFFFFF;
        private static final int SELECTED_COLOR = 0xDDAAAAAA;
        private static final int CAN_CAST_NAME_COLOR = 0xFF00FF00;
        private static final int CANNOT_CAST_NAME_COLOR = 0xFFFF0000;
        private static final int CAN_CAST_COLOR = 0xAA00FF00;
        private static final int CANNOT_CAST_COLOR = 0x88FF0000;
        private final ShinsuTechniques technique;
        private final float angle;
        private final ShinsuSkillWheelGui gui;

        private Part(ShinsuSkillWheelGui gui, @Nullable ShinsuTechniques technique, float angle) {
            this.technique = technique;
            this.angle = angle;
            this.gui = gui;
        }

        private void render(MatrixStack matrixStack) {
            Minecraft client = Minecraft.getInstance();
            client.getTextureManager().bindTexture(TEXTURE);
            MainWindow window = client.getMainWindow();
            int cenX = window.getScaledWidth() / 2;
            int cenY = window.getScaledHeight() / 2;
            int color = ColorHelper.PackedColor.blendColors(equals(gui.selected) ? SELECTED_COLOR : COLOR, canCast() ? CAN_CAST_COLOR : CANNOT_CAST_COLOR);
            matrixStack.push();
            matrixStack.translate(cenX, cenY, 0);
            matrixStack.rotate(new Quaternion(Vector3f.ZP, angle - 90, true));
            RENDER.render(matrixStack, 0, 0, getBlitOffset(), RADIUS, RADIUS, color);
            matrixStack.pop();
        }

        private boolean canCast() {
            Minecraft client = Minecraft.getInstance();
            return technique != null && technique.getBuilder().canCast(technique, client.player, gui.stats.getTechniqueLevel(technique), client.pointedEntity, client.player.getLookVec());
        }

        private void renderName(MatrixStack matrixStack, int x, int y) {
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            drawCenteredString(matrixStack, font, technique == null ? StringTextComponent.EMPTY : technique.getName(), x, y - font.FONT_HEIGHT / 2, canCast() ? CAN_CAST_NAME_COLOR : CANNOT_CAST_NAME_COLOR);
        }
    }
}

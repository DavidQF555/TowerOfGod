package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderInfo;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientCanCastMessage;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientCooldownsMessage;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShinsuSkillWheelGui extends AbstractGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/wheel.png");
    private static final int RADIUS = 100;
    private static final double MIN_MOVEMENT = 1.5;
    public static ShinsuTechnique[] equipped = new ShinsuTechnique[0];
    public static Map<ShinsuTechnique, Integer> cooldowns = Maps.newEnumMap(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Boolean> canCast = Maps.newEnumMap(ShinsuTechnique.class);
    private final Part[] parts;
    private Part selected;
    private float yaw, prevYaw, pitch, prevPitch;
    private boolean locked;

    public ShinsuSkillWheelGui() {
        parts = new Part[(int) (360 / Part.ANGLE)];
        float angle = Part.ANGLE / 2;
        for (int i = 0; i < parts.length; i++) {
            parts[i] = new Part(this, angle);
            angle += Part.ANGLE;
        }
        selected = null;
        yaw = 0;
        prevYaw = 0;
        pitch = 0;
        prevPitch = 0;
        locked = false;
    }

    @Nullable
    public ShinsuTechnique getSelected() {
        return selected == null ? null : selected.technique;
    }

    public void render(MatrixStack matrixStack) {
        Minecraft client = Minecraft.getInstance();
        MainWindow window = client.getMainWindow();
        int cenX = window.getScaledWidth() / 2;
        int cenY = window.getScaledHeight() / 2;
        if (locked && selected != null) {
            selected.renderName(matrixStack, cenX, cenY, 0);
        } else {
            float dYaw = yaw - prevYaw;
            float dPitch = prevPitch - pitch;
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
            for (int i = 0; i < parts.length; i++) {
                parts[i].technique = i < equipped.length ? equipped[i] : null;
                parts[i].render(matrixStack);
            }
            for (Part part : parts) {
                double angle = part.angle - Part.ANGLE / 2;
                int xOff = (int) (Math.cos(angle * Math.PI / 180) * RADIUS / 2);
                int yOff = -(int) (Math.sin(angle * Math.PI / 180) * RADIUS / 2);
                if (part.technique != null) {
                    part.renderName(matrixStack, cenX + xOff, cenY + yOff, cooldowns.getOrDefault(part.technique, 0));
                }
            }
        }
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        prevYaw = yaw;
        yaw = client.player.rotationYawHead;
        prevPitch = pitch;
        pitch = client.player.rotationPitch;
        UpdateClientCooldownsMessage.INSTANCE.sendToServer(new UpdateClientCooldownsMessage());
        UpdateClientCanCastMessage.INSTANCE.sendToServer(new UpdateClientCanCastMessage(client.pointedEntity == null ? null : client.pointedEntity.getUniqueID()));
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
        private static final int COLOR = 0xAAFFFFFF;
        private static final int SELECTED_COLOR = 0xDD888888;
        private static final int CAN_CAST_COLOR = 0xAA00FF00;
        private static final int CANNOT_CAST_COLOR = 0x88FF0000;
        private static final int CAN_CAST_NAME_COLOR = 0xFF00FF00;
        private static final int CANNOT_CAST_NAME_COLOR = 0xFFFF0000;
        private final float angle;
        private final ShinsuSkillWheelGui gui;
        private ShinsuTechnique technique;

        private Part(ShinsuSkillWheelGui gui, float angle) {
            this.angle = angle;
            this.gui = gui;
            technique = null;
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
            matrixStack.rotate(new Quaternion(Vector3f.ZP, -angle, true));
            RENDER.render(matrixStack, 0, 0, getBlitOffset(), RADIUS, RADIUS, color);
            matrixStack.pop();
        }

        private void renderName(MatrixStack matrixStack, int x, int y, int cooldown) {
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            float scale = cooldown <= 0 ? 1 : 0.75f;
            int color = canCast() ? CAN_CAST_NAME_COLOR : CANNOT_CAST_NAME_COLOR;
            drawCenteredString(matrixStack, font, technique == null ? StringTextComponent.EMPTY : technique.getText(), x, y - font.FONT_HEIGHT / 2, color);
            if (cooldown > 0) {
                matrixStack.push();
                matrixStack.scale(scale, scale, scale);
                drawCenteredString(matrixStack, font, technique == null ? StringTextComponent.EMPTY : new StringTextComponent(getRoundedString(cooldown / 20.0, 1) + "s"), (int) (x / scale), (int) ((y + font.FONT_HEIGHT / 2.0) / scale), color);
                matrixStack.pop();
            }
        }

        private String getRoundedString(double cooldown, int digits) {
            String s = cooldown + "";
            String whole = s.substring(0, s.indexOf('.'));
            StringBuilder decimal = new StringBuilder(s.substring(s.indexOf('.') + 1));
            for (int i = decimal.length(); i < digits; i++) {
                decimal.append("0");
            }
            return whole + "." + decimal.substring(0, digits);
        }

        private boolean canCast() {
            return technique != null && canCast.containsKey(technique) && canCast.get(technique);
        }
    }
}

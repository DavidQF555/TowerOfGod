package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientConfigs;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.ServerConfigs;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientErrorPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientShinsuDataPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ShinsuCombinationGui extends AbstractGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/combination.png");
    private static final int ICON_WIDTH = 20, ICON_HEIGHT = 20;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 48, 32, 16, 16, 16, 16);
    private final List<Marker> markers;
    private final int color, text;
    private float prevYaw, prevPitch;
    private ShinsuTechnique selected;
    private int headX, headY, minX, maxX, minY, maxY;

    public ShinsuCombinationGui(float yaw, float pitch) {
        markers = new ArrayList<>();
        prevYaw = yaw;
        prevPitch = pitch;
        color = ClientReference.quality.getColor();
        int alpha = ColorHelper.PackedColor.getAlpha(color);
        int red = ColorHelper.PackedColor.getRed(color);
        int green = ColorHelper.PackedColor.getGreen(color);
        int blue = ColorHelper.PackedColor.getBlue(color);
        if ((red + green + blue) / 3 <= 51) {
            text = ColorHelper.PackedColor.packColor(alpha, red * 6 / 5, blue * 6 / 5, green * 6 / 5);
        } else {
            text = ColorHelper.PackedColor.packColor(alpha, red * 4 / 5, green * 4 / 5, blue * 4 / 5);
        }
        reset();
    }

    public void render(MatrixStack matrixStack, float x, float y) {
        ShinsuTechnique selected = getSelected();
        if (selected == null) {
            for (Marker marker : markers) {
                marker.render(matrixStack, x + (marker.x - minX) * Marker.WIDTH, y + (marker.y - minY) * Marker.HEIGHT);
            }
        } else {
            float centerX = x + getWidth() / 2f;
            float centerY = y + getHeight() / 2f;
            int z = getBlitOffset();
            float iconX = centerX - ICON_WIDTH / 2f;
            float iconY = centerY - ICON_HEIGHT / 2f;
            boolean hasError = ClientReference.ERRORS.containsKey(selected);
            ClientReference.render(BACKGROUND, matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, hasError ? 0xFFFF0000 : color);
            Minecraft client = Minecraft.getInstance();
            if (hasError) {
                ClientReference.render(ShinsuIcons.LOCK, matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF);
                ITextComponent error = ClientReference.ERRORS.get(selected);
                client.fontRenderer.drawText(matrixStack, error, centerX - client.fontRenderer.getStringPropertyWidth(error) / 2f, centerY - client.fontRenderer.FONT_HEIGHT / 2f, 0xFF660000);
            } else {
                ClientReference.render(selected.getIcon(), matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF);
            }
            ITextComponent text = selected.getText().mergeStyle(TextFormatting.BOLD);
            client.fontRenderer.drawText(matrixStack, text, centerX - client.fontRenderer.getStringPropertyWidth(text) / 2f, centerY + ICON_HEIGHT / 2f, hasError ? 0xFF660000 : this.text);
        }
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (getSelected() == null) {
            float dYaw = MathHelper.wrapDegrees(client.player.rotationYaw - prevYaw);
            float dPitch = MathHelper.wrapDegrees(client.player.rotationPitch - prevPitch);
            int resistivity = ClientConfigs.INSTANCE.shinsuCombinationResistivity.get();
            if (dPitch > resistivity) {
                addMarker(Direction.DOWN);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dPitch < -resistivity) {
                addMarker(Direction.UP);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dYaw > resistivity) {
                addMarker(Direction.RIGHT);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dYaw < -resistivity) {
                addMarker(Direction.LEFT);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            }
        }
        if (client.player.world.getGameTime() % ServerConfigs.INSTANCE.shinsuUpdatePeriod.get() == 0) {
            TowerOfGod.CHANNEL.sendToServer(new UpdateClientShinsuDataPacket());
            TowerOfGod.CHANNEL.sendToServer(new UpdateClientErrorPacket());
        }
    }

    protected void reset() {
        headX = 0;
        headY = 0;
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        markers.clear();
    }

    protected void addMarker(Direction direction) {
        headX += direction.getX();
        headY += direction.getY();
        if (headX > maxX) {
            maxX = headX;
        }
        if (headX < minX) {
            minX = headX;
        }
        if (headY > maxY) {
            maxY = headY;
        }
        if (headY < minY) {
            minY = headY;
        }
        if (!markers.isEmpty()) {
            Marker prev = markers.get(markers.size() - 1);
            if (prev.direction == direction) {
                prev.type = Marker.Type.LINE;
            } else {
                float dif = MathHelper.wrapDegrees(direction.getAngle() - prev.direction.getAngle());
                if (dif == 90) {
                    prev.type = Marker.Type.TURN;
                } else if (dif == -90) {
                    prev.type = Marker.Type.TURN;
                    prev.offset = 90;
                } else {
                    prev.type = Marker.Type.REVERSE;
                }
            }
        }
        markers.add(new Marker(headX, headY, Marker.Type.ARROW, direction));
        List<Direction> combination = markers.stream().map(marker -> marker.direction).collect(Collectors.toList());
        if (!combination.isEmpty()) {
            selected = ClientReference.quality.getTechnique(combination);
            if (selected == null) {
                selected = ClientReference.shape.getTechnique(combination);
            }
        }
    }

    @Nullable
    public ShinsuTechnique getSelected() {
        return selected;
    }

    public int getWidth() {
        return markers.size() == 0 ? 0 : (maxX - minX + 1) * Marker.WIDTH;
    }

    public int getHeight() {
        return markers.size() == 0 ? 0 : (maxY - minY + 1) * Marker.HEIGHT;
    }

    private static class Marker extends AbstractGui {

        private static final int WIDTH = 20, HEIGHT = 20;
        private final Direction direction;
        private final int x, y;
        private float offset;
        private Type type;

        private Marker(int x, int y, Type type, Direction direction) {
            this.direction = direction;
            this.x = x;
            this.y = y;
            this.type = type;
        }

        private void render(MatrixStack matrixStack, float x, float y) {
            float centerX = x + WIDTH / 2f;
            float centerY = y + HEIGHT / 2f;
            matrixStack.push();
            matrixStack.translate(centerX, centerY, 0);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(direction.getAngle() + offset + 180));
            matrixStack.translate(-centerX, -centerY, 0);
            ClientReference.render(type.texture, matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, ClientReference.quality.getColor());
            matrixStack.pop();
        }

        private enum Type {

            TURN(new TextureRenderData(TEXTURE, 48, 32, 16, 0, 16, 16)),
            LINE(new TextureRenderData(TEXTURE, 48, 32, 0, 0, 16, 16)),
            REVERSE(new TextureRenderData(TEXTURE, 48, 32, 32, 0, 16, 16)),
            ARROW(new TextureRenderData(TEXTURE, 48, 32, 0, 16, 16, 16));

            private final IRenderData texture;

            Type(IRenderData texture) {
                this.texture = texture;
            }
        }
    }
}

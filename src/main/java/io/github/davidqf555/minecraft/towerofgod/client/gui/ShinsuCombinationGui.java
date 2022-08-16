package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientConfigs;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.ServerConfigs;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientUpdateClientErrorPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ShinsuCombinationGui extends AbstractGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/combination.png");
    private static final int ICON_WIDTH = 20, ICON_HEIGHT = 20;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 48, 32, 16, 16, 16, 16);
    private final List<Marker> markers;
    private final Set<ShinsuTechnique> unlocked;
    private final int color;
    private float prevYaw, prevPitch;
    private ShinsuTechnique selected;
    private int headX, headY, minX, maxX, minY, maxY;

    public ShinsuCombinationGui(Set<ShinsuTechnique> unlocked, float yaw, float pitch) {
        this.unlocked = unlocked;
        markers = new ArrayList<>();
        prevYaw = yaw;
        prevPitch = pitch;
        color = ShinsuQuality.getColor(ClientReference.quality);
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
            BACKGROUND.render(new RenderContext(matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, hasError ? 0xFFFF0000 : color));
            Minecraft client = Minecraft.getInstance();
            if (hasError) {
                ITextComponent error = ClientReference.ERRORS.get(selected);
                client.fontRenderer.drawTextWithShadow(matrixStack, error, centerX - client.fontRenderer.getStringPropertyWidth(error) / 2f, centerY + ICON_HEIGHT / 2f + client.fontRenderer.FONT_HEIGHT + 2, 0xFF660000);
            }
            selected.getIcon().render(new RenderContext(matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF));
            ITextComponent text = selected.getText().mergeStyle(TextFormatting.BOLD);
            client.fontRenderer.drawTextWithShadow(matrixStack, text, centerX - client.fontRenderer.getStringPropertyWidth(text) / 2f, centerY + ICON_HEIGHT / 2f + 1, hasError ? 0xFF660000 : color);
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
            TowerOfGod.CHANNEL.sendToServer(new ClientUpdateClientErrorPacket());
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
            for (ShinsuTechnique technique : unlocked) {
                if (technique.matches(combination)) {
                    selected = technique;
                    break;
                }
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
            type.texture.render(new RenderContext(matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, ShinsuQuality.getColor(ClientReference.quality)));
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

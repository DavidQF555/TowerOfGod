package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientCanCastPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientShinsuDataPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ShinsuCombinationGui extends AbstractGui {

    protected static final String LEVEL = "gui." + TowerOfGod.MOD_ID + ".level_requirement";
    private static final float RESISTIVITY = 20;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/combination.png");
    private static final int BACKGROUND_BLIT_HEIGHT = 16, BACKGROUND_START_Y = 16, ICON_WIDTH = 40, ICON_HEIGHT = 40, BACKGROUND_WIDTH = 60, BACKGROUND_HEIGHT = 60;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 48, 32, 32, BACKGROUND_START_Y, 16, BACKGROUND_BLIT_HEIGHT);
    private final List<Marker> markers;
    private final TextureRenderData cooldown;
    private float prevYaw, prevPitch;
    private ShinsuTechnique selected;
    private int headX, headY, minX, maxX, minY, maxY;

    public ShinsuCombinationGui(float yaw, float pitch) {
        markers = new ArrayList<>();
        prevYaw = yaw;
        prevPitch = pitch;
        cooldown = new TextureRenderData(TEXTURE, 48, 32, 32, BACKGROUND_START_Y, 16, BACKGROUND_BLIT_HEIGHT);
        reset();
    }

    public void render(MatrixStack matrixStack, float x, float y) {
        ShinsuTechnique selected = getSelected();
        if (selected == null) {
            renderCombo(matrixStack, x, y);
        } else {
            ShinsuTechniqueType type = selected.getType();
            float centerX = x + getWidth() / 2f;
            float centerY = y + getHeight() / 2f;
            int z = getBlitOffset();
            float backX = centerX - BACKGROUND_WIDTH / 2f;
            float backY = centerY - BACKGROUND_HEIGHT / 2f;
            boolean canCast = ClientReference.canCast.contains(selected);
            ClientReference.render(BACKGROUND, matrixStack, backX, backY, z, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, canCast ? 0xFF6DC0FF : 0xFFFF4444);
            float percent = ClientReference.INITIAL_COOLDOWNS.containsKey(selected) && ClientReference.data.containsKey(type) ? ClientReference.data.get(type).getCooldown() * 1f / ClientReference.INITIAL_COOLDOWNS.get(selected) : 1;
            int blitHeight = (int) (BACKGROUND_BLIT_HEIGHT * percent);
            cooldown.setBlitHeight(blitHeight);
            cooldown.setStartY(BACKGROUND_START_Y + BACKGROUND_BLIT_HEIGHT - blitHeight);
            int height = (int) (BACKGROUND_HEIGHT * percent);
            ClientReference.render(cooldown, matrixStack, backX, backY + BACKGROUND_HEIGHT - height, z, BACKGROUND_WIDTH, height, 0x55000000);
            float iconX = centerX - ICON_WIDTH / 2f;
            float iconY = centerY - ICON_HEIGHT / 2f;
            Minecraft client = Minecraft.getInstance();
            int req = selected.getLevelRequirement();
            if (!ClientReference.data.containsKey(type) || ClientReference.data.get(type).getLevel() < req) {
                ClientReference.render(ShinsuIcons.LOCK, matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF);
                ITextComponent text = new TranslationTextComponent(LEVEL, req, type.getText());
                client.fontRenderer.drawText(matrixStack, text, centerX - client.fontRenderer.getStringPropertyWidth(text) / 2f, centerY - client.fontRenderer.FONT_HEIGHT / 2f, 0xFFFF0000);
            } else {
                ClientReference.render(selected.getIcon(), matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF);
            }
            ITextComponent text = selected.getText();
            client.fontRenderer.drawText(matrixStack, text, centerX - client.fontRenderer.getStringPropertyWidth(text) / 2f, centerY + ICON_HEIGHT / 2f, canCast ? 0xFF0797FF : 0xFFFF0000);
        }
    }

    protected void renderCombo(MatrixStack matrixStack, float x, float y) {
        for (Marker marker : markers) {
            marker.render(matrixStack, x + (marker.x - minX) * Marker.WIDTH, y + (marker.y - minY) * Marker.HEIGHT);
        }
    }

    public void tick() {
        if (getSelected() == null) {
            Minecraft client = Minecraft.getInstance();
            float dYaw = MathHelper.wrapDegrees(client.player.rotationYaw - prevYaw);
            float dPitch = MathHelper.wrapDegrees(client.player.rotationPitch - prevPitch);
            if (dPitch > RESISTIVITY) {
                addMarker(Direction.DOWN);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dPitch < -RESISTIVITY) {
                addMarker(Direction.UP);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dYaw > RESISTIVITY) {
                addMarker(Direction.RIGHT);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            } else if (dYaw < -RESISTIVITY) {
                addMarker(Direction.LEFT);
                prevYaw = client.player.rotationYaw;
                prevPitch = client.player.rotationPitch;
            }
        }
        TowerOfGod.CHANNEL.sendToServer(new UpdateClientShinsuDataPacket());
        TowerOfGod.CHANNEL.sendToServer(new UpdateClientCanCastPacket());
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
                    prev.type = Marker.Type.REVERSE_TURN;
                } else {
                    prev.type = Marker.Type.REVERSE;
                }
            }
        }
        markers.add(new Marker(headX, headY, Marker.Type.ARROW, direction));
        List<Direction> combination = markers.stream().map(marker -> marker.direction).collect(Collectors.toList());
        if (!combination.isEmpty()) {
            for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
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
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(direction.getAngle() + 180));
            matrixStack.translate(-centerX, -centerY, 0);
            ClientReference.render(type.texture, matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, 0xFFFFFFFF);
            matrixStack.pop();
        }

        private enum Type {

            TURN(new TextureRenderData(TEXTURE, 48, 32, 16, 0, 16, 16)),
            REVERSE_TURN(new TextureRenderData(TEXTURE, 48, 32, 16, 16, 16, 16)),
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

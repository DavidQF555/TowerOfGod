package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.client.ClientConfigs;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.CastShinsuPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientOpenCombinationGUIPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientUpdateClientErrorPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ShinsuCombinationGui implements IIngameOverlay {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/combination.png");
    private static final int ICON_WIDTH = 20, ICON_HEIGHT = 20;
    private static final TextureRenderData BACKGROUND = new TextureRenderData(TEXTURE, 48, 32, 16, 16, 16, 16);
    private final List<Marker> markers;
    private final Set<ShinsuTechnique> unlocked;
    private final int color;
    private boolean enabled;
    private float prevYaw, prevPitch;
    private ShinsuTechnique selected;
    private int headX, headY, minX, maxX, minY, maxY;

    public ShinsuCombinationGui() {
        this.unlocked = new HashSet<>();
        markers = new ArrayList<>();
        color = ShinsuAttribute.getColor(ClientReference.attribute);
        reset();
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !client.player.isSpectator()) {
            if (KeyBindingsList.SHINSU_TECHNIQUE_GUI.isDown()) {
                if (!enabled) {
                    TowerOfGod.CHANNEL.sendToServer(new ClientOpenCombinationGUIPacket());
                    prevYaw = client.player.yHeadRot;
                    prevPitch = client.player.getXRot();
                    enabled = true;
                }
                int x = (screenWidth - getWidth()) / 2;
                int y = (screenHeight - getHeight()) / 2 - 50;
                if (selected == null) {
                    for (Marker marker : markers) {
                        marker.render(matrixStack, x + (marker.x - minX) * Marker.WIDTH, y + (marker.y - minY) * Marker.HEIGHT, gui.getBlitOffset());
                    }
                } else {
                    float centerX = x + getWidth() / 2f;
                    float centerY = y + getHeight() / 2f;
                    int z = gui.getBlitOffset();
                    float iconX = centerX - ICON_WIDTH / 2f;
                    float iconY = centerY - ICON_HEIGHT / 2f;
                    boolean hasError = ClientReference.ERRORS.containsKey(selected);
                    BACKGROUND.render(new RenderContext(matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, hasError ? 0xFFFF0000 : color));
                    if (hasError) {
                        Component error = ClientReference.ERRORS.get(selected);
                        client.font.drawShadow(matrixStack, error, centerX - client.font.width(error) / 2f, centerY + ICON_HEIGHT / 2f + client.font.lineHeight + 2, 0xFF660000);
                    }
                    selected.getIcon().render(new RenderContext(matrixStack, iconX, iconY, z, ICON_WIDTH, ICON_HEIGHT, 0xFFFFFFFF));
                    Component text = selected.getText().withStyle(ChatFormatting.BOLD);
                    client.font.drawShadow(matrixStack, text, centerX - client.font.width(text) / 2f, centerY + ICON_HEIGHT / 2f + 1, hasError ? 0xFF660000 : color);
                }
            } else {
                if (selected != null) {
                    TowerOfGod.CHANNEL.sendToServer(new CastShinsuPacket(selected));
                }
                reset();
            }
        }
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (selected == null) {
            float dYaw = Mth.wrapDegrees(client.player.yHeadRot - prevYaw);
            float dPitch = Mth.wrapDegrees(client.player.getXRot() - prevPitch);
            int resistivity = ClientConfigs.INSTANCE.shinsuCombinationResistivity.get();
            if (dPitch > resistivity) {
                addMarker(Direction.DOWN);
                prevYaw = client.player.yHeadRot;
                prevPitch = client.player.getXRot();
            } else if (dPitch < -resistivity) {
                addMarker(Direction.UP);
                prevYaw = client.player.yHeadRot;
                prevPitch = client.player.getXRot();
            } else if (dYaw > resistivity) {
                addMarker(Direction.RIGHT);
                prevYaw = client.player.yHeadRot;
                prevPitch = client.player.getXRot();
            } else if (dYaw < -resistivity) {
                addMarker(Direction.LEFT);
                prevYaw = client.player.yHeadRot;
                prevPitch = client.player.getXRot();
            }
        }
        TowerOfGod.CHANNEL.sendToServer(new ClientUpdateClientErrorPacket());
    }

    public void setUnlocked(Set<ShinsuTechnique> techniques) {
        unlocked.clear();
        unlocked.addAll(techniques);
    }

    protected void reset() {
        headX = 0;
        headY = 0;
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        markers.clear();
        selected = null;
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
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
                float dif = Mth.wrapDegrees(direction.getAngle() - prev.direction.getAngle());
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

    public int getWidth() {
        return markers.size() == 0 ? 0 : (maxX - minX + 1) * Marker.WIDTH;
    }

    public int getHeight() {
        return markers.size() == 0 ? 0 : (maxY - minY + 1) * Marker.HEIGHT;
    }

    private static class Marker {

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

        private void render(PoseStack matrixStack, float x, float y, float z) {
            float centerX = x + WIDTH / 2f;
            float centerY = y + HEIGHT / 2f;
            matrixStack.pushPose();
            matrixStack.translate(centerX, centerY, 0);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(direction.getAngle() + offset + 180));
            matrixStack.translate(-centerX, -centerY, 0);
            type.texture.render(new RenderContext(matrixStack, x, y, z, WIDTH, HEIGHT, ShinsuAttribute.getColor(ClientReference.attribute)));
            matrixStack.popPose();
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

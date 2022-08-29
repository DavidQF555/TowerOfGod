package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.towerofgod.client.gui.GuideScreen;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuCombinationGui;
import io.github.davidqf555.minecraft.towerofgod.client.gui.StatsMeterGui;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.data.ItemStackRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ClientReference {

    public static final Map<ShinsuTechnique, Component> ERRORS = new HashMap<>();
    public static StatsMeterGui shinsu = null;
    public static StatsMeterGui baangs = null;
    public static ShinsuCombinationGui combo = null;
    public static ShinsuAttribute attribute = null;

    private ClientReference() {
    }

    public static void renderTextureData(TextureRenderData data, RenderContext context) {
        Matrix4f matrix = context.getPoseStack().last().pose();
        float x = context.getX();
        float y = context.getY();
        int width = context.getWidth();
        int height = context.getHeight();
        float x2 = x + width;
        float y2 = y + height;
        int color = context.getColor();
        int a = FastColor.ARGB32.alpha(color);
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        float minU = data.getStartX() * 1f / data.getTextureWidth();
        float maxU = (data.getStartX() + data.getBlitWidth()) * 1f / data.getTextureWidth();
        float minV = data.getStartY() * 1f / data.getTextureHeight();
        float maxV = (data.getStartY() + data.getBlitHeight()) * 1f / data.getTextureHeight();
        float blitOffset = context.getBlitOffset();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix, x, y2, blitOffset).color(r, g, b, a).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, blitOffset).color(r, g, b, a).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y, blitOffset).color(r, g, b, a).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, x, y, blitOffset).color(r, g, b, a).uv(minU, minV).endVertex();
        bufferbuilder.end();
        RenderSystem.enableBlend();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
    }

    public static void renderItemStackData(ItemStackRenderData data, RenderContext context) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(data.get(), (int) context.getX(), (int) context.getY());
    }

    public static void openCombinationGUI(Set<ShinsuTechnique> unlocked) {
        Player player = Minecraft.getInstance().player;
        ClientReference.combo = new ShinsuCombinationGui(unlocked, player.yHeadRot, player.getViewXRot(1));
    }

    public static void openGuideScreen(ShinsuTechnique[] pages, int color) {
        Minecraft.getInstance().setScreen(new GuideScreen(pages, 221, 180, color));
    }

    public static void updateDimensions(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

}

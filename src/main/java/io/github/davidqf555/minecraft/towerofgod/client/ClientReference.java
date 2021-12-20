package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuCombinationGui;
import io.github.davidqf555.minecraft.towerofgod.client.gui.StatsMeterGui;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.data.ItemStackRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public final class ClientReference {

    public static final Map<ShinsuTechnique, ITextComponent> ERRORS = new EnumMap<>(ShinsuTechnique.class);
    public static StatsMeterGui shinsu = null;
    public static StatsMeterGui baangs = null;
    public static ShinsuCombinationGui combo = null;
    public static ShinsuQuality quality = ShinsuQuality.NONE;
    public static ShinsuShape shape = ShinsuShape.NONE;

    private ClientReference() {
    }

    public static void renderTextureData(TextureRenderData data, RenderContext context) {
        Matrix4f matrix = context.getMatrixStack().getLast().getMatrix();
        float x = context.getX();
        float y = context.getY();
        int width = context.getWidth();
        int height = context.getHeight();
        float x2 = x + width;
        float y2 = y + height;
        int color = context.getColor();
        int a = ColorHelper.PackedColor.getAlpha(color);
        int r = ColorHelper.PackedColor.getRed(color);
        int g = ColorHelper.PackedColor.getGreen(color);
        int b = ColorHelper.PackedColor.getBlue(color);
        float minU = data.getStartX() * 1f / data.getTextureWidth();
        float maxU = (data.getStartX() + data.getBlitWidth()) * 1f / data.getTextureWidth();
        float minV = data.getStartY() * 1f / data.getTextureHeight();
        float maxV = (data.getStartY() + data.getBlitHeight()) * 1f / data.getTextureHeight();
        float blitOffset = context.getBlitOffset();
        Minecraft.getInstance().getTextureManager().bindTexture(data.getTexture());
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(matrix, x, y2, blitOffset).color(r, g, b, a).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, x2, y2, blitOffset).color(r, g, b, a).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, x2, y, blitOffset).color(r, g, b, a).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, x, y, blitOffset).color(r, g, b, a).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableBlend();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.disableBlend();
    }

    public static void renderItemStackData(ItemStackRenderData data, RenderContext context) {
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(data.get(), (int) context.getX(), (int) context.getY());
    }

}

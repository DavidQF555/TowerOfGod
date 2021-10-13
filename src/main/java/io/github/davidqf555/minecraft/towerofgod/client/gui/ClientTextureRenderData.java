package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientTextureRenderData extends TextureRenderData implements IClientRenderData {

    public ClientTextureRenderData(ResourceLocation texture, int textureWidth, int textureHeight, int startX, int startY, int blitWidth, int blitHeight) {
        super(texture, textureWidth, textureHeight, startX, startY, blitWidth, blitHeight);
    }

    public static ClientTextureRenderData copy(TextureRenderData data) {
        return new ClientTextureRenderData(data.getTexture(), data.getTextureWidth(), data.getTextureHeight(), data.getStartX(), data.getStartY(), data.getBlitWidth(), data.getBlitHeight());
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color) {
        Matrix4f matrix = matrixStack.getLast().getMatrix();
        float x2 = x + width;
        float y2 = y + height;
        int a = ColorHelper.PackedColor.getAlpha(color);
        int r = ColorHelper.PackedColor.getRed(color);
        int g = ColorHelper.PackedColor.getGreen(color);
        int b = ColorHelper.PackedColor.getBlue(color);
        float minU = startX * 1f / textureWidth;
        float maxU = (startX + blitWidth) * 1f / textureWidth;
        float minV = startY * 1f / textureHeight;
        float maxV = (startY + blitHeight) * 1f / textureHeight;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
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

    public void setStartX(int x) {
        startX = x;
    }

    public void setStartY(int y) {
        startY = y;
    }

    public void setBlitWidth(int width) {
        blitWidth = width;
    }

    public void setBlitHeight(int height) {
        blitHeight = height;
    }
}

package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuTechniqueBarGui;
import io.github.davidqf555.minecraft.towerofgod.client.gui.StatsMeterGui;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ItemStackRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ClientReference {

    public static final Map<ShinsuTechnique, Integer> INITIAL_COOLDOWNS = new EnumMap<>(ShinsuTechnique.class);
    public static StatsMeterGui shinsu = null;
    public static StatsMeterGui baangs = null;
    public static ShinsuTechniqueBarGui bar = null;
    public static Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
    public static List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
    public static Map<ShinsuTechnique, Integer> cooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);

    public static void render(IRenderData data, MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color) {
        if (data instanceof TextureRenderData) {
            Matrix4f matrix = matrixStack.getLast().getMatrix();
            float x2 = x + width;
            float y2 = y + height;
            int a = ColorHelper.PackedColor.getAlpha(color);
            int r = ColorHelper.PackedColor.getRed(color);
            int g = ColorHelper.PackedColor.getGreen(color);
            int b = ColorHelper.PackedColor.getBlue(color);
            TextureRenderData tex = (TextureRenderData) data;
            float minU = tex.getStartX() * 1f / tex.getTextureWidth();
            float maxU = (tex.getStartX() + tex.getBlitWidth()) * 1f / tex.getTextureWidth();
            float minV = tex.getStartY() * 1f / tex.getTextureHeight();
            float maxV = (tex.getStartY() + tex.getBlitHeight()) * 1f / tex.getTextureHeight();
            Minecraft.getInstance().getTextureManager().bindTexture(tex.getTexture());
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
        } else if (data instanceof ItemStackRenderData) {
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(((ItemStackRenderData) data).get(), (int) x, (int) y);
        }
    }

}

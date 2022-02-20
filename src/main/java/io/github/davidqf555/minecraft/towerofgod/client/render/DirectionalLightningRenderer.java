package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class DirectionalLightningRenderer extends EntityRenderer<DirectionalLightningBoltEntity> {

    public DirectionalLightningRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(DirectionalLightningBoltEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Vector3f dif = entityIn.getStart();
        float totalHeightRadius = entityIn.getHeight() / 2f;
        dif.sub(new Vector3f(entityIn.getPositionVec().add(0, totalHeightRadius, 0)));
        float length = MathHelper.sqrt(dif.getX() * dif.getX() + dif.getY() * dif.getY() + dif.getZ() * dif.getZ());
        int segments = getSegments(length);
        Vector3f[] vertexes = new Vector3f[segments];
        float sumX = 0;
        float sumY = 0;
        float depthDif = length / segments;
        float var = getMaxVariation(length);
        Random random = new Random(entityIn.boltVertex);
        for (int i = 1; i < segments; i++) {
            vertexes[i] = new Vector3f(random.nextFloat() * var * 2 - var, random.nextFloat() * var * 2 - var, depthDif);
            sumX += vertexes[i].getX();
            sumY += vertexes[i].getY();
        }
        int fixed = random.nextInt(vertexes.length);
        vertexes[0] = vertexes[fixed];
        vertexes[fixed] = new Vector3f(-sumX, -sumY, depthDif);
        matrixStackIn.push();
        float yaw = (float) Math.PI / 2 - (float) MathHelper.atan2(-dif.getZ(), dif.getX());
        float pitch = (float) Math.asin(dif.getY() / length);
        matrixStackIn.rotate(Vector3f.YN.rotation(yaw));
        matrixStackIn.rotate(Vector3f.XP.rotation(pitch));
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getLightning());
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        int color = getColor();
        float alpha = ColorHelper.PackedColor.getAlpha(color) / 255f;
        float red = ColorHelper.PackedColor.getRed(color) / 255f;
        float green = ColorHelper.PackedColor.getGreen(color) / 255f;
        float blue = ColorHelper.PackedColor.getBlue(color) / 255f;
        int layers = getLayers();
        float totalWidthRadius = entityIn.getWidth() / 2f;
        for (int i = 0; i < layers; i++) {
            float widthRadius = 0.1f + (totalWidthRadius - 0.1f) * i / layers;
            float heightRadius = 0.1f + (totalHeightRadius - 0.1f) * i / layers;
            float prevX = 0;
            float prevY = 0;
            float prevZ = 0;
            for (Vector3f vertex : vertexes) {
                float x = prevX - vertex.getX();
                float y = prevY - vertex.getY();
                float z = prevZ - vertex.getZ();
                builder.pos(matrix4f, prevX - widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x - widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x + widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX + widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX + widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x + widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x + widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX + widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX + widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x + widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x - widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX - widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX - widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x - widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, x - widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.pos(matrix4f, prevX - widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
        matrixStackIn.pop();
    }

    protected int getSegments(float length) {
        return 2 + (int) length / 16;
    }

    protected int getLayers() {
        return 4;
    }

    protected int getColor() {
        return 0x4D737380;
    }

    protected float getMaxVariation(float length) {
        return 1 + length / 16;
    }

    @Override
    public ResourceLocation getEntityTexture(DirectionalLightningBoltEntity entity) {
        return null;
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class DirectionalLightningRenderer extends EntityRenderer<DirectionalLightningBoltEntity> {

    public DirectionalLightningRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(DirectionalLightningBoltEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Vector3f dif = entityIn.getStart();
        float totalHeightRadius = entityIn.getBbHeight() / 2f;
        dif.sub(new Vector3f((float) entityIn.getX(), (float) entityIn.getY() + totalHeightRadius, (float) entityIn.getZ()));
        float length = Mth.sqrt(dif.x() * dif.x() + dif.y() * dif.y() + dif.z() * dif.z());
        int segments = getSegments(length);
        Vector3f[] vertexes = new Vector3f[segments];
        float sumX = 0;
        float sumY = 0;
        float depthDif = length / segments;
        float var = getMaxVariation(length);
        Random random = new Random(entityIn.seed);
        for (int i = 1; i < segments; i++) {
            vertexes[i] = new Vector3f(random.nextFloat() * var * 2 - var, random.nextFloat() * var * 2 - var, depthDif);
            sumX += vertexes[i].x();
            sumY += vertexes[i].y();
        }
        int fixed = random.nextInt(vertexes.length);
        vertexes[0] = vertexes[fixed];
        vertexes[fixed] = new Vector3f(-sumX, -sumY, depthDif);
        matrixStackIn.pushPose();
        float yaw = (float) Math.PI / 2 - (float) Mth.atan2(-dif.z(), dif.x());
        float pitch = (float) Math.asin(dif.y() / length);
        matrixStackIn.mulPose(Vector3f.YN.rotation(yaw));
        matrixStackIn.mulPose(Vector3f.XP.rotation(pitch));
        VertexConsumer builder = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStackIn.last().pose();
        int color = getColor();
        float alpha = FastColor.ARGB32.alpha(color) / 255f;
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;
        int layers = getLayers();
        float totalWidthRadius = entityIn.getBbWidth() / 2f;
        for (int i = 0; i < layers; i++) {
            float widthRadius = 0.1f + (totalWidthRadius - 0.1f) * i / layers;
            float heightRadius = 0.1f + (totalHeightRadius - 0.1f) * i / layers;
            float prevX = 0;
            float prevY = 0;
            float prevZ = 0;
            for (Vector3f vertex : vertexes) {
                float x = prevX - vertex.x();
                float y = prevY - vertex.y();
                float z = prevZ - vertex.z();
                builder.vertex(matrix4f, prevX - widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x - widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x + widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX + widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX + widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x + widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x + widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX + widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX + widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x + widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x - widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX - widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX - widthRadius, prevY + heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x - widthRadius, y + heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, x - widthRadius, y - heightRadius, z).color(red, green, blue, alpha).endVertex();
                builder.vertex(matrix4f, prevX - widthRadius, prevY - heightRadius, prevZ).color(red, green, blue, alpha).endVertex();
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
        matrixStackIn.popPose();
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
    public ResourceLocation getTextureLocation(DirectionalLightningBoltEntity entity) {
        return null;
    }
}

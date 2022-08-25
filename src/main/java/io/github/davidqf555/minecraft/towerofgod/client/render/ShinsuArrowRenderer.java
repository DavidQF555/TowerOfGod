package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShinsuArrowRenderer extends EntityRenderer<ShinsuArrowEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu_arrow.png");

    public ShinsuArrowRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(ShinsuArrowEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int hex = ShinsuQuality.getColor(entityIn.getQuality());
        int red = ColorHelper.PackedColor.red(hex);
        int green = ColorHelper.PackedColor.green(hex);
        int blue = ColorHelper.PackedColor.blue(hex);
        int alpha = ColorHelper.PackedColor.alpha(hex);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));
        float shake = entityIn.shakeTime - partialTicks;
        if (shake > 0) {
            float shakeRotation = -MathHelper.sin(shake * 3) * shake;
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(shakeRotation));
        }
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.scale(0.05625F, 0.05625F, 0.05625F);
        matrixStackIn.translate(-4, 0, 0);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
        MatrixStack.Entry entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLightIn);
        drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLightIn);
        for (int i = 0; i < 4; i++) {
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
            drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLightIn);
            drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLightIn);
            drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLightIn);
            drawColoredVertex(matrix4f, matrix3f, ivertexbuilder, red, green, blue, alpha, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLightIn);
        }
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void drawColoredVertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder vertexBuilder, int red, int green, int blue, int alpha, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float p_229039_9_, float p_229039_10_, float p_229039_11_, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(red, green, blue, alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, p_229039_9_, p_229039_11_, p_229039_10_).endVertex();
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nullable ShinsuArrowEntity entity) {
        return TEXTURE;
    }
}

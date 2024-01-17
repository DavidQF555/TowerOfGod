package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShinsuArrowRenderer extends EntityRenderer<ShinsuArrowEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu_arrow.png");

    public ShinsuArrowRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(ShinsuArrowEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int hex = ShinsuAttribute.getColor(entityIn.getAttribute());
        int red = FastColor.ARGB32.red(hex);
        int green = FastColor.ARGB32.green(hex);
        int blue = FastColor.ARGB32.blue(hex);
        int alpha = FastColor.ARGB32.alpha(hex);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        float shake = entityIn.shakeTime - partialTicks;
        if (shake > 0) {
            float shakeRotation = -Mth.sin(shake * 3) * shake;
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(shakeRotation));
        }
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.scale(0.05625F, 0.05625F, 0.05625F);
        matrixStackIn.translate(-4, 0, 0);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
        PoseStack.Pose entry = matrixStackIn.last();
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

    private void drawColoredVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, int red, int green, int blue, int alpha, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float p_229039_9_, float p_229039_10_, float p_229039_11_, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(red, green, blue, alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, p_229039_9_, p_229039_11_, p_229039_10_).endVertex();
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nullable ShinsuArrowEntity entity) {
        return TEXTURE;
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class ShinsuRenderer extends EntityRenderer<ShinsuEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu.png");
    private static final RenderType TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public ShinsuRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ShinsuEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@Nonnull ShinsuEntity entityIn, float entityYaw, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn) {
        int hex = ShinsuAttribute.getColor(null);
        int red = ColorHelper.PackedColor.red(hex);
        int green = ColorHelper.PackedColor.green(hex);
        int blue = ColorHelper.PackedColor.blue(hex);
        int alpha = ColorHelper.PackedColor.alpha(hex);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
        IVertexBuilder builder = bufferIn.getBuffer(TYPE);
        MatrixStack.Entry entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        builder.vertex(matrix4f, -1, 1.5f, 0).color(red, green, blue, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, 1, 1.5f, 0).color(red, green, blue, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, 1, -0.5f, 0).color(red, green, blue, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, -1, -0.5f, 0).color(red, green, blue, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

}

package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

public class ShinsuRenderer extends EntityRenderer<ShinsuEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu.png");
    private static final RenderType TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public ShinsuRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ShinsuEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@Nonnull ShinsuEntity entityIn, float entityYaw, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn) {
        int hex = ShinsuAttribute.getColor(null);
        int red = FastColor.ARGB32.red(hex);
        int green = FastColor.ARGB32.green(hex);
        int blue = FastColor.ARGB32.blue(hex);
        int alpha = FastColor.ARGB32.alpha(hex);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
        VertexConsumer builder = bufferIn.getBuffer(TYPE);
        PoseStack.Pose entry = matrixStackIn.last();
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

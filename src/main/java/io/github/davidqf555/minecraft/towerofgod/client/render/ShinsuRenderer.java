package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
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

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu_entity.png");
    private static final RenderType TYPE = RenderType.getEntityCutoutNoCull(TEXTURE);

    public ShinsuRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull ShinsuEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@Nonnull ShinsuEntity entityIn, float entityYaw, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn) {
        int hex = entityIn.getQuality().getColor();
        int red = ColorHelper.PackedColor.getRed(hex);
        int green = ColorHelper.PackedColor.getGreen(hex);
        int blue = ColorHelper.PackedColor.getBlue(hex);
        int alpha = ColorHelper.PackedColor.getAlpha(hex);
        matrixStackIn.push();
        matrixStackIn.rotate(renderManager.getCameraOrientation());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180f));
        IVertexBuilder builder = bufferIn.getBuffer(TYPE);
        MatrixStack.Entry entry = matrixStackIn.getLast();
        Matrix4f matrix4f = entry.getMatrix();
        Matrix3f matrix3f = entry.getNormal();
        builder.pos(matrix4f, -1, 1.5f, 0).color(red, green, blue, alpha).tex(0, 1).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.pos(matrix4f, 1, 1.5f, 0).color(red, green, blue, alpha).tex(1, 1).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.pos(matrix4f, 1, -0.5f, 0).color(red, green, blue, alpha).tex(1, 0).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        builder.pos(matrix4f, -1, -0.5f, 0).color(red, green, blue, alpha).tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(matrix3f, 0, 1, 0).endVertex();
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

}

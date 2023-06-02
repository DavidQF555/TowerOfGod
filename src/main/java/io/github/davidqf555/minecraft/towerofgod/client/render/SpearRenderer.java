package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import io.github.davidqf555.minecraft.towerofgod.common.entities.SpearEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SpearRenderer<T extends SpearEntity> extends EntityRenderer<T> {

    private final SpearModel model = new SpearModel();

    public SpearRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int hex = getColor(entityIn);
        float red = ColorHelper.PackedColor.red(hex) / 255f;
        float green = ColorHelper.PackedColor.green(hex) / 255f;
        float blue = ColorHelper.PackedColor.blue(hex) / 255f;
        float alpha = ColorHelper.PackedColor.alpha(hex) / 255f;
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot) + 90));
        IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, model.renderType(getTextureLocation(entityIn)), false, entityIn.hasEffect());
        model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return model.getTextureLocation(entity.getPickupItem().getItem());
    }

    protected int getColor(T entity) {
        return 0xFFFFFFFF;
    }

}

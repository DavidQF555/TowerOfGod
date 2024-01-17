package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.SpearEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class SpearRenderer<T extends SpearEntity> extends EntityRenderer<T> {

    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(new ResourceLocation(TowerOfGod.MOD_ID, "spear"), "main");
    private final SpearModel model;

    public SpearRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        model = new SpearModel(renderManagerIn.bakeLayer(LOCATION));
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int hex = getColor(entityIn);
        float red = FastColor.ARGB32.red(hex) / 255f;
        float green = FastColor.ARGB32.green(hex) / 255f;
        float blue = FastColor.ARGB32.blue(hex) / 255f;
        float alpha = FastColor.ARGB32.alpha(hex) / 255f;
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()) + 90));
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, model.renderType(getTextureLocation(entityIn)), false, entityIn.hasEffect());
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

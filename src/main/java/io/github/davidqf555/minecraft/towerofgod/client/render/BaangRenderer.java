package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.BaangEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class BaangRenderer extends EntityRenderer<BaangEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu.png");

    public BaangRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f last, Matrix3f normal, int light, float x, float y, float uv1, float uv2, int alpha, int red, int blue, int green) {
        consumer.vertex(last, x - 0.5f, y - 0.25f, 0)
                .color(alpha, red, blue, green)
                .uv(uv1, uv2)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    @Override
    public void render(BaangEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int color = ShinsuAttribute.getColor(entityIn.getTechniqueType().getConfig().getDisplay().attribute().orElse(null));
        int alpha = FastColor.ARGB32.alpha(color);
        int red = FastColor.ARGB32.red(color);
        int blue = FastColor.ARGB32.blue(color);
        int green = FastColor.ARGB32.green(color);
        VertexConsumer consumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        matrixStackIn.pushPose();
        matrixStackIn.scale(2.0F, 2.0F, 2.0F);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = matrixStackIn.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        vertex(consumer, matrix4f, matrix3f, packedLightIn, 0, 0, 0, 1, alpha, red, blue, green);
        vertex(consumer, matrix4f, matrix3f, packedLightIn, 1, 0, 1, 1, alpha, red, blue, green);
        vertex(consumer, matrix4f, matrix3f, packedLightIn, 1, 1, 1, 0, alpha, red, blue, green);
        vertex(consumer, matrix4f, matrix3f, packedLightIn, 0, 1, 0, 0, alpha, red, blue, green);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(BaangEntity entity) {
        return TEXTURE;
    }

}

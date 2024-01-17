package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ClickerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClickerRenderer extends EntityRenderer<ClickerEntity> {

    public ClickerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(ClickerEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        ItemStack item = entityIn.getShape().getItem();
        ShinsuAttribute.setAttribute(item, entityIn.getAttribute());
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entityIn.getYRot()));
        matrixStackIn.translate(0, 0.25, 0);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, entityIn.getId());
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(ClickerEntity entity) {
        return null;
    }
}

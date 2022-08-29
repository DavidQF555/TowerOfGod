package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class SpearItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    public static final SpearItemStackRenderer INSTANCE = new SpearItemStackRenderer();
    private final SpearModel spear;

    public SpearItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        spear = new SpearModel(Minecraft.getInstance().getEntityModels().bakeLayer(SpearRenderer.LOCATION));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        VertexConsumer builder = ItemRenderer.getFoilBufferDirect(buffer, spear.renderType(spear.getTextureLocation(stack.getItem())), false, stack.hasFoil());
        spear.renderToBuffer(matrixStack, builder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.popPose();
    }
}

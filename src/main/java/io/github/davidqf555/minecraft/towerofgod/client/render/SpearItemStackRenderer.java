package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class SpearItemStackRenderer extends ItemStackTileEntityRenderer {

    private final SpearModel spear;

    public SpearItemStackRenderer() {
        spear = new SpearModel();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transforms, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        IVertexBuilder builder = ItemRenderer.getFoilBufferDirect(buffer, spear.renderType(spear.getTextureLocation(stack.getItem())), false, stack.hasFoil());
        spear.renderToBuffer(matrixStack, builder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.popPose();
    }
}

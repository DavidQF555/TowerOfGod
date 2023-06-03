package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ColorHelper;

public class SpearItemStackRenderer extends ItemStackTileEntityRenderer {

    private final SpearModel spear;

    public SpearItemStackRenderer() {
        spear = new SpearModel();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transforms, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        int color = Minecraft.getInstance().getItemRenderer().itemColors.getColor(stack, 0);
        float red = ColorHelper.PackedColor.red(color) / 255f;
        float green = ColorHelper.PackedColor.green(color) / 255f;
        float blue = ColorHelper.PackedColor.blue(color) / 255f;
        float alpha = ColorHelper.PackedColor.alpha(color) / 255f;
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        IVertexBuilder builder = ItemRenderer.getFoilBufferDirect(buffer, spear.renderType(spear.getTextureLocation(stack.getItem())), false, stack.hasFoil());
        spear.renderToBuffer(matrixStack, builder, combinedLight, combinedOverlay, red, green, blue, alpha);
        matrixStack.popPose();
    }

}

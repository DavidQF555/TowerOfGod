package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class SpearTileEntityRenderer extends ItemStackTileEntityRenderer {

    private final SpearModel spear;

    public SpearTileEntityRenderer() {
        spear = new SpearModel();
    }

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transforms, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        matrixStack.scale(1, -1, -1);
        IVertexBuilder builder = ItemRenderer.getEntityGlintVertexBuilder(buffer, spear.getRenderType(spear.getTextureLocation(((SpearItem) stack.getItem()).getTier())), false, stack.hasEffect());
        spear.render(matrixStack, builder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.pop();
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpearItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    public static final SpearItemStackRenderer INSTANCE = new SpearItemStackRenderer();
    private SpearModel spear;

    public SpearItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        spear = new SpearModel(Minecraft.getInstance().getEntityModels().bakeLayer(SpearRenderer.LOCATION));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transforms, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        int color = Minecraft.getInstance().getItemRenderer().itemColors.getColor(stack, 0);
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;
        float alpha = FastColor.ARGB32.alpha(color) / 255f;
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        VertexConsumer builder = ItemRenderer.getFoilBufferDirect(buffer, spear.renderType(spear.getTextureLocation(stack.getItem())), false, stack.hasFoil());
        spear.renderToBuffer(matrixStack, builder, combinedLight, combinedOverlay, red, green, blue, alpha);
        matrixStack.popPose();
    }

}

package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.ModToolTier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;

public class SpearModel extends Model {

    private static final ResourceLocation WOOD = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation STONE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation IRON = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation GOLD = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation DIAMOND = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation NETHERITE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private static final ResourceLocation SUSPENDIUM = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/spear/wooden_spear.png");
    private final ModelRenderer spear;

    public SpearModel() {
        super(RenderType::getEntitySolid);
        spear = new ModelRenderer(18, 26, 0, 0);
        spear.addBox(-0.5f, 2, -0.5f, 1, 25, 1);
        ModelRenderer point1 = new ModelRenderer(18, 26, 4, 0);
        point1.addBox(-1, 1, -0.5f, 2, 1, 1);
        spear.addChild(point1);
        ModelRenderer point2 = new ModelRenderer(18, 26, 4, 2);
        point2.addBox(-2, 0, -0.5f, 4, 1, 1);
        spear.addChild(point2);
        ModelRenderer point3 = new ModelRenderer(18, 26, 4, 4);
        point3.addBox(-3, -1, -0.5f, 6, 1, 1);
        spear.addChild(point3);
        ModelRenderer point4 = new ModelRenderer(18, 26, 4, 6);
        point4.addBox(-2.5f, -2, -0.5f, 5, 1, 1);
        spear.addChild(point4);
        ModelRenderer point5 = new ModelRenderer(18, 26, 4, 8);
        point5.addBox(-2, -3, -0.5f, 4, 1, 1);
        spear.addChild(point5);
        ModelRenderer point6 = new ModelRenderer(18, 26, 4, 10);
        point6.addBox(-1.5f, -4, -0.5f, 3, 1, 1);
        spear.addChild(point6);
        ModelRenderer point7 = new ModelRenderer(18, 26, 4, 12);
        point7.addBox(-1, -5, -0.5f, 2, 1, 1);
        spear.addChild(point7);
        ModelRenderer point8 = new ModelRenderer(18, 26, 4, 14);
        point8.addBox(-0.5f, -6, -0.5f, 1, 1, 1);
        spear.addChild(point8);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        spear.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public ResourceLocation getTextureLocation(IItemTier tier) {
        if (tier.equals(ItemTier.WOOD)) {
            return WOOD;
        } else if (tier.equals(ItemTier.STONE)) {
            return STONE;
        } else if (tier.equals(ItemTier.GOLD)) {
            return GOLD;
        } else if (tier.equals(ItemTier.DIAMOND)) {
            return DIAMOND;
        } else if (tier.equals(ItemTier.NETHERITE)) {
            return NETHERITE;
        } else if (tier.equals(ModToolTier.SUSPENDIUM)) {
            return SUSPENDIUM;
        } else {
            return IRON;
        }
    }
}

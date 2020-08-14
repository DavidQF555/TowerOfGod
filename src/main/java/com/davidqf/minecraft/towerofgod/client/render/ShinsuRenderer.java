package com.davidqf.minecraft.towerofgod.client.render;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.model.ShinsuModel;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ShinsuRenderer extends EntityRenderer<ShinsuEntity> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/shinsu_entity.png");
    private static final ShinsuModel<ShinsuEntity> MODEL = new ShinsuModel<>();

    public ShinsuRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull ShinsuEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@Nonnull ShinsuEntity entityIn, float entityYaw, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        boolean visible = !entityIn.isInvisible() && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(MODEL.getRenderType(getEntityTexture(entityIn)));
        int overlay = OverlayTexture.getPackedUV(OverlayTexture.getU(0), OverlayTexture.getV(false));
        int hex = entityIn.getQuality().getColor();
        int red = (hex & 0xFF0000) >> 16;
        int green = (hex & 0xFF00) >> 8;
        int blue = (hex & 0xFF);
        MODEL.render(matrixStackIn, ivertexbuilder, packedLightIn, overlay, (float) (red / 255.0) + 1, (float) (green / 255.0) + 1, (float) (blue / 255.0) + 1, visible ? 0.15F : 1.0F);
    }

}

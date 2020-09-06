package com.davidqf.minecraft.towerofgod.client.model;

import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class ShinsuModel extends EntityModel<ShinsuEntity> {

    private final ModelRenderer shinsu;

    public ShinsuModel() {
        textureWidth = 32;
        textureHeight = 32;
        shinsu = new ModelRenderer(this);
        shinsu.setRotationPoint(0.0F, 4.0F, 0.0F);
        shinsu.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(@Nonnull ShinsuEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        shinsu.rotateAngleX = headPitch * ((float) Math.PI / 140f);
        shinsu.rotateAngleY = netHeadYaw * ((float) Math.PI / 140f);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        shinsu.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}

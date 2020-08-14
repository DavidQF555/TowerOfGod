package com.davidqf.minecraft.towerofgod.client.model;

import com.davidqf.minecraft.towerofgod.entities.LighthouseEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class LighthouseModel<T extends LighthouseEntity> extends EntityModel<T> {

    private final ModelRenderer lighthouse;

    public LighthouseModel() {
        textureWidth = 64;
        textureHeight = 64;
        lighthouse = new ModelRenderer(this);
        lighthouse.setRotationPoint(0.0F, 16.0F, 0.0F);
        lighthouse.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        lighthouse.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        lighthouse.rotateAngleX = headPitch * ((float) Math.PI / 180f);
        lighthouse.rotateAngleY = netHeadYaw * ((float) Math.PI / 180f);
    }
}

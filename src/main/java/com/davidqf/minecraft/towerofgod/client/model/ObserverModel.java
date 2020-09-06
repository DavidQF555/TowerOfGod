package com.davidqf.minecraft.towerofgod.client.model;

import com.davidqf.minecraft.towerofgod.entities.ObserverEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class ObserverModel extends EntityModel<ObserverEntity> {

    private final ModelRenderer observer;

    public ObserverModel() {
        textureWidth = 32;
        textureHeight = 32;
        observer = new ModelRenderer(this);
        observer.setRotationPoint(0.0F, 24.0F, 0.0F);
        observer.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        observer.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(@Nonnull ObserverEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        observer.rotateAngleX = headPitch * ((float) Math.PI / 180f);
        observer.rotateAngleY = netHeadYaw * ((float) Math.PI / 180f);
    }
}

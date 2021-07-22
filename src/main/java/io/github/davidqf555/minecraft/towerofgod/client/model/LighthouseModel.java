package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class LighthouseModel extends EntityModel<LighthouseEntity> {

    private final ModelRenderer lighthouse;

    public LighthouseModel() {
        textureWidth = 64;
        textureHeight = 32;
        lighthouse = new ModelRenderer(this);
        lighthouse.setRotationPoint(0, 16, 0);
        lighthouse.setTextureOffset(0, 0).addBox(-8, -8, -8, 16, 16, 16, 0, false);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        lighthouse.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(@Nonnull LighthouseEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        lighthouse.rotateAngleX = headPitch * ((float) Math.PI / 180f);
        lighthouse.rotateAngleY = netHeadYaw * ((float) Math.PI / 180f);
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class CastingArmsModel<T extends PlayerEntity> extends EntityModel<T> {

    private final ModelRenderer rightArm, leftArm;

    public CastingArmsModel(BipedModel<T> parent) {
        rightArm = parent.rightArm;
        leftArm = parent.leftArm;
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm.visible = true;
        leftArm.visible = true;
        CastingModelHelper.transformLeftArm(leftArm);
        CastingModelHelper.transformRightArm(rightArm);
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rightArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class CastingArmsModel<T extends Player> extends EntityModel<T> {

    private final ModelPart rightArm, leftArm;

    public CastingArmsModel(HumanoidModel<T> parent) {
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
    public void renderToBuffer(@Nonnull PoseStack matrixStack, @Nonnull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rightArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

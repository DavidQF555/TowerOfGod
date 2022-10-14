package io.github.davidqf555.minecraft.towerofgod.client.model;

import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;

public class BipedShinsuUserModel<T extends Mob & IShinsuUser> extends HumanoidModel<T> {

    public BipedShinsuUserModel(ModelPart part) {
        super(part);
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        rightArmPose = HumanoidModel.ArmPose.EMPTY;
        leftArmPose = HumanoidModel.ArmPose.EMPTY;
        if (entityIn.isAggressive() && entityIn.getMainHandItem().getItem() instanceof BowItem) {
            if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn.isCasting()) {
            CastingModelHelper.transformRightArm(rightArm);
            CastingModelHelper.transformLeftArm(leftArm);
        }
    }
}

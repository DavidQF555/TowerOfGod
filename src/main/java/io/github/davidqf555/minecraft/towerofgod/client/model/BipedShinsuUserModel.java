package io.github.davidqf555.minecraft.towerofgod.client.model;

import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.HandSide;

public class BipedShinsuUserModel<T extends MobEntity & IShinsuUser> extends BipedModel<T> {

    public BipedShinsuUserModel(float modelSize, int width, int height) {
        super(modelSize, 0, width, height);
    }

    public BipedShinsuUserModel(float modelSize) {
        super(modelSize, 0, 64, 32);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        rightArmPose = BipedModel.ArmPose.EMPTY;
        leftArmPose = BipedModel.ArmPose.EMPTY;
        if (entityIn.isAggressive() && entityIn.getHeldItemMainhand().getItem() instanceof BowItem) {
            if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
                rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }
}

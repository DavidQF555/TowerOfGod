package io.github.davidqf555.minecraft.towerofgod.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;

public final class CastingModelHelper {

    private CastingModelHelper() {
    }

    public static void transformRightArm(ModelRenderer rightArm) {
        rightArm.yRot = 0;
        rightArm.xRot = -60 * (float) Math.PI / 180;
        rightArm.zRot = -20 * (float) Math.PI / 180;
    }

    public static void transformLeftArm(ModelRenderer leftArm) {
        leftArm.yRot = 0;
        leftArm.xRot = -60 * (float) Math.PI / 180;
        leftArm.zRot = 20 * (float) Math.PI / 180;
    }

}

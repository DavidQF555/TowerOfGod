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
        texWidth = 64;
        texHeight = 32;
        lighthouse = new ModelRenderer(this);
        lighthouse.setPos(0, 16, 0);
        lighthouse.texOffs(0, 0).addBox(-8, -8, -8, 16, 16, 16, 0, false);
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        lighthouse.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(@Nonnull LighthouseEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        lighthouse.xRot = headPitch * ((float) Math.PI / 180f);
        lighthouse.yRot = netHeadYaw * ((float) Math.PI / 180f);
    }
}

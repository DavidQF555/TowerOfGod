package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class ObserverModel extends EntityModel<ObserverEntity> {

    private final ModelRenderer observer;

    public ObserverModel() {
        texWidth = 32;
        texHeight = 16;
        observer = new ModelRenderer(this);
        observer.setPos(0, 24, 0);
        observer.texOffs(0, 0).addBox(-4, -8, -4, 8, 8, 8, 0, false);
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        observer.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(@Nonnull ObserverEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        observer.xRot = headPitch * ((float) Math.PI / 180f);
        observer.yRot = netHeadYaw * ((float) Math.PI / 180f);
    }
}

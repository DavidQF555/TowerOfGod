package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import javax.annotation.Nonnull;

public class ObserverModel extends EntityModel<ObserverEntity> {

    private final ModelPart observer;

    public ObserverModel(ModelPart part) {
        observer = part.getChild("observer");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("observer", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4, -8, -4, 8, 8, 8), PartPose.offset(0, 24, 0));
        return LayerDefinition.create(mesh, 32, 16);
    }

    @Override
    public void renderToBuffer(@Nonnull PoseStack matrixStack, @Nonnull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        observer.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(@Nonnull ObserverEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        observer.xRot = headPitch * ((float) Math.PI / 180f);
        observer.yRot = netHeadYaw * ((float) Math.PI / 180f);
    }
}

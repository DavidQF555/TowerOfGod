package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import javax.annotation.Nonnull;

public class LighthouseModel extends EntityModel<LighthouseEntity> {

    private final ModelPart lighthouse;

    public LighthouseModel(ModelPart part) {
        lighthouse = part.getChild("lighthouse");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("lighthouse", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-8, -8, -8, 16, 16, 16), PartPose.offset(0, 16, 0));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(@Nonnull PoseStack matrixStack, @Nonnull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        lighthouse.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(@Nonnull LighthouseEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        lighthouse.xRot = headPitch * ((float) Math.PI / 180f);
        lighthouse.yRot = netHeadYaw * ((float) Math.PI / 180f);
    }
}

package io.github.davidqf555.minecraft.towerofgod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class SpearModel extends Model {

    private final ModelPart spear;

    public SpearModel(ModelPart part) {
        super(RenderType::entityTranslucent);
        spear = part.getChild("spear");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("spear", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.5f, 2, -0.5f, 1, 25, 1)
                        .texOffs(4, 0).addBox(-1, 1, -0.5f, 2, 1, 1)
                        .texOffs(4, 2).addBox(-2, 0, -0.5f, 4, 1, 1)
                        .texOffs(4, 4).addBox(-3, -1, -0.5f, 6, 1, 1)
                        .texOffs(4, 6).addBox(-2.5f, -2, -0.5f, 5, 1, 1)
                        .texOffs(4, 8).addBox(-2, -3, -0.5f, 4, 1, 1)
                        .texOffs(4, 10).addBox(-1.5f, -4, -0.5f, 3, 1, 1)
                        .texOffs(4, 12).addBox(-1, -5, -0.5f, 2, 1, 1)
                        .texOffs(4, 14).addBox(-0.5f, -6, -0.5f, 1, 1, 1)
                , PartPose.ZERO);
        return LayerDefinition.create(mesh, 18, 26);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        spear.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public ResourceLocation getTextureLocation(Item item) {
        ResourceLocation registry = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(registry.getNamespace(), "textures/entity/spear/" + registry.getPath() + ".png");
    }

}

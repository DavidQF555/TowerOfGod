package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.model.CastingArmsModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;

public class CastingLayerRenderer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private final CastingArmsModel<T> model;

    public CastingLayerRenderer(RenderLayerParent<T, M> parent) {
        super(parent);
        model = new CastingArmsModel<>(getParentModel());
    }

    @Override
    public void render(PoseStack matrix, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible() && ClientReference.isCasting(entity)) {
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            model.renderToBuffer(matrix, buffer.getBuffer(model.renderType(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }
    }

}

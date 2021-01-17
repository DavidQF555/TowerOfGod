package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class RegularRenderer extends BipedRenderer<RegularEntity, BipedModel<RegularEntity>> {

    public RegularRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedModel<>(RenderType::getEntityCutoutNoCull, 1, 0, 64, 64), 0.5f);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(RegularEntity entity) {
        return entity.getFamily().getTexture();
    }

}

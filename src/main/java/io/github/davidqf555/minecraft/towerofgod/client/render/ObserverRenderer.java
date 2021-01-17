package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.model.ObserverModel;

import io.github.davidqf555.minecraft.towerofgod.common.entities.ObserverEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ObserverRenderer extends MobRenderer<ObserverEntity, ObserverModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/observer_entity.png");

    public ObserverRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ObserverModel(), 0.1f);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull ObserverEntity entity) {
        return TEXTURE;
    }

}

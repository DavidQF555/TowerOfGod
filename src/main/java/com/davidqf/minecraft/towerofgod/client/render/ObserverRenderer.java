package com.davidqf.minecraft.towerofgod.client.render;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.model.ObserverModel;

import com.davidqf.minecraft.towerofgod.entities.ObserverEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ObserverRenderer extends MobRenderer<ObserverEntity, ObserverModel<ObserverEntity>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/observer_entity.png");

    public ObserverRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ObserverModel<>(), 0.1f);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull ObserverEntity entity) {
        return TEXTURE;
    }

}

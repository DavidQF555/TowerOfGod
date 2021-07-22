package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.model.ObserverModel;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ObserverRenderer extends DeviceRenderer<ObserverEntity, ObserverModel> {

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

package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.client.model.ObserverModel;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ObserverRenderer extends DeviceRenderer<ObserverEntity, ObserverModel> {

    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(new ResourceLocation(TowerOfGod.MOD_ID, "observer"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/observer.png");

    public ObserverRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ObserverModel(renderManagerIn.bakeLayer(LOCATION)), 0.1f);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ObserverEntity entity) {
        return TEXTURE;
    }

}

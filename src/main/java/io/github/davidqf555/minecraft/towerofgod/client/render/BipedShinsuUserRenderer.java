package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.client.model.BipedShinsuUserModel;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BipedShinsuUserRenderer<T extends MobEntity & IShinsuUser<T>> extends BipedRenderer<T, BipedShinsuUserModel<T>> {

    public BipedShinsuUserRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedShinsuUserModel<>(0, 64, 64), 0.5f);
        addLayer(new BipedArmorLayer<>(this, new BipedShinsuUserModel<>(0.5f), new BipedShinsuUserModel<>(1)));
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return entity.getGroup().getTexture();
    }
}
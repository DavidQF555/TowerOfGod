package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.client.model.BipedShinsuUserModel;
import io.github.davidqf555.minecraft.towerofgod.common.entities.Group;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nonnull;

public class BipedShinsuUserRenderer<T extends Mob & IShinsuUser> extends HumanoidMobRenderer<T, BipedShinsuUserModel<T>> {

    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/steve.png");

    public BipedShinsuUserRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BipedShinsuUserModel<>(renderManagerIn.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
        addLayer(new HumanoidArmorLayer<>(this, new BipedShinsuUserModel<>(renderManagerIn.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)), new BipedShinsuUserModel<>(renderManagerIn.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR))));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        Group group = entity.getGroup();
        return group == null ? DEFAULT : group.getTexture();
    }

}

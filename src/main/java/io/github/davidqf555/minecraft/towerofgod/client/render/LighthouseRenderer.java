package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.client.model.LighthouseModel;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LighthouseRenderer extends DeviceRenderer<LighthouseEntity, LighthouseModel> {

    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/lighthouse.png");

    public LighthouseRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LighthouseModel(renderManagerIn.bakeLayer(LOCATION)), 0.5f);
    }

    @Override
    public int getBlockLightLevel(@Nullable LighthouseEntity entityIn, @Nullable BlockPos partialTicks) {
        return 15;
    }

    @Override
    protected float getAlpha(boolean invisible) {
        return invisible ? super.getAlpha(true) : 0.75f;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull LighthouseEntity entity) {
        return TEXTURE;
    }

}

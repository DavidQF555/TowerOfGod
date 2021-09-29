package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.model.LighthouseModel;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LighthouseRenderer extends DeviceRenderer<LighthouseEntity, LighthouseModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/lighthouse_entity.png");

    public LighthouseRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new LighthouseModel(), 0.5f);
    }

    @Override
    public int getBlockLight(@Nullable LighthouseEntity entityIn, @Nullable BlockPos partialTicks) {
        return 15;
    }

    @Override
    protected float getAlpha(boolean invisible) {
        return invisible ? super.getAlpha(true) : 0.75f;
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull LighthouseEntity entity) {
        return TEXTURE;
    }

}

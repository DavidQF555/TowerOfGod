package com.davidqf.minecraft.towerofgod.client.render;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.model.LighthouseModel;
import com.davidqf.minecraft.towerofgod.common.entities.LighthouseEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LighthouseRenderer extends MobRenderer<LighthouseEntity, LighthouseModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/lighthouse_entity.png");

    public LighthouseRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new LighthouseModel(), 0.5f);
    }

    @Override
    public int getBlockLight(@Nullable LighthouseEntity entityIn, @Nullable BlockPos partialTicks) {
        return 15;
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull LighthouseEntity entity) {
        return TEXTURE;
    }

}

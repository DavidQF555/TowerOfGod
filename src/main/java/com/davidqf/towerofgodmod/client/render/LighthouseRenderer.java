package com.davidqf.towerofgodmod.client.render;

import com.davidqf.towerofgodmod.TowerOfGod;
import com.davidqf.towerofgodmod.client.model.LighthouseModel;
import com.davidqf.towerofgodmod.entities.LighthouseEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class LighthouseRenderer extends MobRenderer<LighthouseEntity, LighthouseModel<LighthouseEntity>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/lighthouse_entity.png"); 

	public LighthouseRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new LighthouseModel<>(), 0.5f);
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull LighthouseEntity entity) {
		return TEXTURE;
	}

}

package com.davidqf.towerofgodmod.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class LighthouseEntity extends FlyingEntity {

	private LivingEntity owner;

	public LighthouseEntity(EntityType<? extends FlyingEntity> type, World worldIn, LivingEntity owner) {
		super(type, worldIn);
		this.owner = owner;
	}

	public static AttributeModifierMap.MutableAttribute setAttributes(){
		return func_233666_p_();
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.BLOCK_BEACON_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource s) {
		return SoundEvents.BLOCK_BEACON_POWER_SELECT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_BEACON_DEACTIVATE;
	}

	public static class Factory implements EntityType.IFactory<LighthouseEntity> {
		@Override
		public LighthouseEntity create(EntityType<LighthouseEntity> type, World world) {
			return new LighthouseEntity(type, world, null);
		}
	}
}

package com.davidqf.towerofgodmod.items;

import com.davidqf.towerofgodmod.entities.LighthouseEntity;
import com.davidqf.towerofgodmod.util.RegistryHandler;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LighthouseItem extends BasicItem {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		Vector3d spawn = playerIn.getEyePosition(0).add(playerIn.getLookVec().mul(2, 2, 2));
		LighthouseEntity light = new LighthouseEntity(RegistryHandler.LIGHTHOUSE_ENTITY.get(), worldIn, playerIn);
		if(light.canSpawn(worldIn, SpawnReason.MOB_SUMMONED)) {
			light.setPosition(spawn.x, spawn.y, spawn.z);
			worldIn.addEntity(light);
			ItemStack item = playerIn.getHeldItem(handIn);
			item.setCount(item.getCount() - 1);
			playerIn.setHeldItem(handIn, item);
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}
}

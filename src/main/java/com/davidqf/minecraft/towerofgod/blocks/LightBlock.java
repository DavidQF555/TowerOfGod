package com.davidqf.minecraft.towerofgod.blocks;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class LightBlock extends AirBlock {

	private static final int LIGHT = 15;

	public LightBlock() {
		super(Block.Properties.create(Material.AIR)
				.func_235838_a_(state -> LIGHT));
	}

}

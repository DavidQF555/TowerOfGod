package com.davidqf.towerofgodmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SuspendiumOre extends Block {

	public SuspendiumOre() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(5f, 5f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE));
	}

}

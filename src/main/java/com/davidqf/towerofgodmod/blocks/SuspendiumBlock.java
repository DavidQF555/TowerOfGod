package com.davidqf.towerofgodmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SuspendiumBlock extends Block {

	public SuspendiumBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3f, 3f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE));
	}

}

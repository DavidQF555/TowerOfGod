package com.davidqf.towerofgodmod.items;

import com.davidqf.towerofgodmod.TowerOfGod;

import net.minecraft.block.Block;
import net.minecraft.item.*;

public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block blockIn) {
		super(blockIn, new Item.Properties().group(TowerOfGod.TAB));
	}

}

package com.davidqf.minecraft.towerofgod.common.items;

import com.davidqf.minecraft.towerofgod.TowerOfGod;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block blockIn) {
		super(blockIn, new Item.Properties().group(TowerOfGod.TAB));
	}

}

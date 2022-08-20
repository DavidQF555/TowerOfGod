package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;

import java.util.List;

public interface DropsFilter {

    DropsFilter NONE = (context, original) -> original;

    List<ItemStack> apply(LootContext context, List<ItemStack> original);

}

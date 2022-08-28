package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public interface DropsFilter {

    DropsFilter NONE = (context, original) -> original;

    List<ItemStack> apply(LootContext context, List<ItemStack> original);

}

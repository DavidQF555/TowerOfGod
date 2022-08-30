package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public interface DropsFilter {

    DropsFilter NONE = (context, original) -> original;

    ObjectArrayList<ItemStack> apply(LootContext context, ObjectArrayList<ItemStack> original);

}

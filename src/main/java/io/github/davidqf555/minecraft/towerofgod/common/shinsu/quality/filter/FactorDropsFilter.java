package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FactorDropsFilter implements DropsFilter {

    private final Function<ItemStack, Double> factor;

    public FactorDropsFilter(Function<ItemStack, Double> factor) {
        this.factor = factor;
    }

    @Override
    public List<ItemStack> apply(LootContext context, List<ItemStack> original) {
        List<ItemStack> increased = new ArrayList<>();
        for (ItemStack drop : original) {
            drop.setCount((int) (drop.getCount() * factor.apply(drop)));
            increased.add(drop);
        }
        return increased;
    }
}

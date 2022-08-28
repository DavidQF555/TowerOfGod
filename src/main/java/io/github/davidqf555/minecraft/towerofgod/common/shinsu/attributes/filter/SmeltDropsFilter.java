package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class SmeltDropsFilter implements DropsFilter {

    public static final SmeltDropsFilter INSTANCE = new SmeltDropsFilter();

    protected SmeltDropsFilter() {
    }

    @Override
    public List<ItemStack> apply(LootContext context, List<ItemStack> original) {
        Level world = context.getLevel();
        List<ItemStack> smelted = new ArrayList<>();
        RecipeManager manager = context.getLevel().getRecipeManager();
        for (ItemStack drop : original) {
            smelted.add(manager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(drop), world).map(SmeltingRecipe::getResultItem).filter(item -> !item.isEmpty()).map(item -> ItemHandlerHelper.copyStackWithSize(item, drop.getCount() * item.getCount())).orElse(drop));
        }
        return smelted;
    }

}

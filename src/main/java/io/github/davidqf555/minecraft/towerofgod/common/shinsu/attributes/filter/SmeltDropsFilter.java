package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootContext;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class SmeltDropsFilter implements DropsFilter {

    public static final SmeltDropsFilter INSTANCE = new SmeltDropsFilter();

    protected SmeltDropsFilter() {
    }

    @Override
    public List<ItemStack> apply(LootContext context, List<ItemStack> original) {
        World world = context.getLevel();
        List<ItemStack> smelted = new ArrayList<>();
        RecipeManager manager = context.getLevel().getRecipeManager();
        for (ItemStack drop : original) {
            smelted.add(manager.getRecipeFor(IRecipeType.SMELTING, new Inventory(drop), world).map(FurnaceRecipe::getResultItem).filter(item -> !item.isEmpty()).map(item -> ItemHandlerHelper.copyStackWithSize(item, drop.getCount() * item.getCount())).orElse(drop));
        }
        return smelted;
    }

}

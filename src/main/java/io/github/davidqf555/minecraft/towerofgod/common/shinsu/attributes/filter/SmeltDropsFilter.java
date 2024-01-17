package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.filter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.items.ItemHandlerHelper;

public class SmeltDropsFilter implements DropsFilter {

    public static final SmeltDropsFilter INSTANCE = new SmeltDropsFilter();

    protected SmeltDropsFilter() {
    }

    @Override
    public ObjectArrayList<ItemStack> apply(LootContext context, ObjectArrayList<ItemStack> original) {
        ServerLevel world = context.getLevel();
        ObjectArrayList<ItemStack> smelted = new ObjectArrayList<>();
        RecipeManager manager = context.getLevel().getRecipeManager();
        for (ItemStack drop : original) {
            smelted.add(manager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(drop), world)
                    .map(RecipeHolder::value)
                    .map(recipe -> recipe.getResultItem(world.getServer().registryAccess()))
                    .filter(item -> !item.isEmpty())
                    .map(item -> ItemHandlerHelper.copyStackWithSize(item, drop.getCount() * item.getCount()))
                    .orElse(drop)
            );
        }
        return smelted;
    }

}

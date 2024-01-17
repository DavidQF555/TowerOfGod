package io.github.davidqf555.minecraft.towerofgod.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.data.*;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;
import java.util.function.Consumer;

public class DataGenRecipeProvider extends RecipeProvider {

    private static final List<Pair<Either<IItemProvider, ITag<Item>>, RegistryObject<SpearItem>>> CRAFTABLE_SPEARS = ImmutableList.of(
            Pair.of(Either.left(Items.STICK), ItemRegistry.WOODEN_SPEAR),
            Pair.of(Either.right(ItemTags.STONE_TOOL_MATERIALS), ItemRegistry.STONE_SPEAR),
            Pair.of(Either.left(Items.IRON_INGOT), ItemRegistry.IRON_SPEAR),
            Pair.of(Either.left(Items.GOLD_INGOT), ItemRegistry.GOLDEN_SPEAR),
            Pair.of(Either.left(Items.DIAMOND), ItemRegistry.DIAMOND_SPEAR),
            Pair.of(Either.left(Items.NETHERITE_INGOT), ItemRegistry.NETHERITE_SPEAR),
            Pair.of(Either.left(ItemRegistry.SUSPENDIUM::get), ItemRegistry.SUSPENDIUM_SPEAR)
    );
    private static final List<Pair<Either<IItemProvider, ITag<Item>>, RegistryObject<HookItem>>> CRAFTABLE_HOOKS = ImmutableList.of(
            Pair.of(Either.right(ItemTags.PLANKS), ItemRegistry.WOODEN_HOOK),
            Pair.of(Either.right(ItemTags.STONE_TOOL_MATERIALS), ItemRegistry.STONE_HOOK),
            Pair.of(Either.left(Items.IRON_INGOT), ItemRegistry.IRON_HOOK),
            Pair.of(Either.left(Items.GOLD_INGOT), ItemRegistry.GOLDEN_HOOK),
            Pair.of(Either.left(Items.DIAMOND), ItemRegistry.DIAMOND_HOOK),
            Pair.of(Either.left(Items.NETHERITE_INGOT), ItemRegistry.NETHERITE_HOOK),
            Pair.of(Either.left(ItemRegistry.SUSPENDIUM::get), ItemRegistry.SUSPENDIUM_HOOK)
    );
    private static final List<Pair<Either<IItemProvider, ITag<Item>>, RegistryObject<NeedleItem>>> CRAFTABLE_NEEDLES = ImmutableList.of(
            Pair.of(Either.left(Items.IRON_INGOT), ItemRegistry.WOODEN_NEEDLE),
            Pair.of(Either.right(ItemTags.STONE_TOOL_MATERIALS), ItemRegistry.STONE_NEEDLE),
            Pair.of(Either.left(Items.IRON_INGOT), ItemRegistry.IRON_NEEDLE),
            Pair.of(Either.left(Items.GOLD_INGOT), ItemRegistry.GOLDEN_NEEDLE),
            Pair.of(Either.left(Items.DIAMOND), ItemRegistry.DIAMOND_NEEDLE),
            Pair.of(Either.left(Items.NETHERITE_INGOT), ItemRegistry.NETHERITE_NEEDLE),
            Pair.of(Either.left(ItemRegistry.SUSPENDIUM::get), ItemRegistry.SUSPENDIUM_NEEDLE)
    );


    public DataGenRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        CRAFTABLE_NEEDLES.forEach(pair -> {
            RegistryObject<NeedleItem> registry = pair.getSecond();
            NeedleItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_NEEDLE.get()), material, item)
                        .unlocks("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern("x  ")
                        .pattern(" x ")
                        .pattern("  y")
                        .define('x', material)
                        .define('y', Items.IRON_INGOT)
                        .unlockedBy("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer);
            }
        });
        CRAFTABLE_HOOKS.forEach(pair -> {
            RegistryObject<HookItem> registry = pair.getSecond();
            HookItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_HOOK.get()), material, item)
                        .unlocks("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("x  ")
                        .define('x', material)
                        .unlockedBy("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer);
            }
        });
        CRAFTABLE_SPEARS.forEach(pair -> {
            RegistryObject<SpearItem> registry = pair.getSecond();
            SpearItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_SPEAR.get()), material, item)
                        .unlocks("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern(" xx")
                        .pattern(" yx")
                        .pattern("y  ")
                        .define('x', material)
                        .define('y', Items.STICK)
                        .unlockedBy("has_material", pair.getFirst().map(RecipeProvider::has, RecipeProvider::has))
                        .save(consumer);
            }
        });
    }

}

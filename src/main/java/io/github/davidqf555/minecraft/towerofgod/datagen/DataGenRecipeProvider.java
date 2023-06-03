package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tags.ITag;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DataGenRecipeProvider extends RecipeProvider {

    public DataGenRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        for (RegistryObject<NeedleItem> registry : ItemRegistry.NEEDLE_ITEMS) {
            NeedleItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_NEEDLE.get()), material, item)
                        .unlocks("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern("x  ")
                        .pattern(" x ")
                        .pattern("  y")
                        .define('x', material)
                        .define('y', Items.IRON_INGOT)
                        .unlockedBy("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer);
            }
        }
        for (RegistryObject<? extends HookItem> registry : ItemRegistry.CRAFTABLE_HOOKS) {
            HookItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_HOOK.get()), material, item)
                        .unlocks("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("x  ")
                        .define('x', material)
                        .unlockedBy("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer);
            }
        }
        for (RegistryObject<? extends SpearItem> registry : ItemRegistry.CRAFTABLE_SPEARS) {
            SpearItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_SPEAR.get()), material, item)
                        .unlocks("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(item)
                        .pattern(" xx")
                        .pattern(" yx")
                        .pattern("y  ")
                        .define('x', material)
                        .define('y', Items.STICK)
                        .unlockedBy("has_material", inventoryTrigger(getPredicates(material)))
                        .save(consumer);
            }
        }
    }

    private ItemPredicate[] getPredicates(Ingredient ingredient) {
        Set<ITag<Item>> tags = new HashSet<>();
        Set<ItemStack> items = new HashSet<>();
        for (Ingredient.IItemList list : ingredient.values) {
            if (list instanceof Ingredient.TagList) {
                tags.add(((Ingredient.TagList) list).tag);
            } else {
                items.addAll(list.getItems());
            }
        }
        ItemPredicate[] predicates = new ItemPredicate[tags.size() + items.size()];
        int i = 0;
        for (ITag<Item> tag : tags) {
            predicates[i] = ItemPredicate.Builder.item().of(tag).build();
            i++;
        }
        for (ItemStack item : items) {
            ItemPredicate.Builder builder = ItemPredicate.Builder.item().of(item.getItem());
            CompoundNBT nbt = item.getTag();
            if (nbt != null) {
                builder.hasNbt(nbt);
            }
            for (INBT tag : item.getEnchantmentTags()) {
                Enchantment enchantment = Enchantment.byId(((CompoundNBT) tag).getInt("id"));
                int level = ((CompoundNBT) tag).getInt("lvl");
                builder.hasEnchantment(new EnchantmentPredicate(enchantment, MinMaxBounds.IntBound.atLeast(level)));
            }
            predicates[i] = ItemPredicate.Builder.item().of(item.getItem()).hasNbt(item.getTag()).build();
            i++;
        }
        return predicates;
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.data.gen;

import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.registration.ItemRegistry;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DataGenRecipeProvider extends RecipeProvider {

    public DataGenRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for (RegistryObject<NeedleItem> registry : ItemRegistry.NEEDLE_ITEMS) {
            NeedleItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairMaterial();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithingRecipe(Ingredient.fromItems(ItemRegistry.DIAMOND_NEEDLE.get()), material, item)
                        .addCriterion("has_material", hasItem(getPredicates(material)))
                        .build(consumer, ForgeRegistries.ITEMS.getKey(item));

            } else {
                ShapedRecipeBuilder.shapedRecipe(item)
                        .patternLine("x  ")
                        .patternLine(" x ")
                        .patternLine("  y")
                        .key('x', material)
                        .key('y', Items.IRON_INGOT)
                        .addCriterion("has_material", hasItem(getPredicates(material)))
                        .build(consumer);
            }
        }
        for (RegistryObject<HookItem> registry : ItemRegistry.HOOK_ITEMS) {
            HookItem item = registry.get();
            IItemTier tier = item.getTier();
            Ingredient material = tier.getRepairMaterial();
            if (tier.equals(ItemTier.NETHERITE)) {
                SmithingRecipeBuilder.smithingRecipe(Ingredient.fromItems(ItemRegistry.DIAMOND_HOOK.get()), material, item)
                        .addCriterion("has_material", hasItem(getPredicates(material)))
                        .build(consumer, ForgeRegistries.ITEMS.getKey(item));

            } else {
                ShapedRecipeBuilder.shapedRecipe(item)
                        .patternLine("xxx")
                        .patternLine("x x")
                        .patternLine("x  ")
                        .key('x', material)
                        .addCriterion("has_material", hasItem(getPredicates(material)))
                        .build(consumer);
            }
        }
    }

    private ItemPredicate[] getPredicates(Ingredient ingredient) {
        Set<ITag<Item>> tags = new HashSet<>();
        Set<ItemStack> items = new HashSet<>();
        for (Ingredient.IItemList list : ingredient.acceptedItems) {
            if (list instanceof Ingredient.TagList) {
                tags.add(((Ingredient.TagList) list).tag);
            } else {
                items.addAll(list.getStacks());
            }
        }
        ItemPredicate[] predicates = new ItemPredicate[tags.size() + items.size()];
        int i = 0;
        for (ITag<Item> tag : tags) {
            predicates[i] = ItemPredicate.Builder.create().tag(tag).build();
            i++;
        }
        for (ItemStack item : items) {
            ItemPredicate.Builder builder = ItemPredicate.Builder.create().item(item.getItem());
            CompoundNBT nbt = item.getTag();
            if (nbt != null) {
                builder.nbt(nbt);
            }
            for (INBT tag : item.getEnchantmentTagList()) {
                Enchantment enchantment = Enchantment.getEnchantmentByID(((CompoundNBT) tag).getInt("id"));
                int level = ((CompoundNBT) tag).getInt("lvl");
                builder.enchantment(new EnchantmentPredicate(enchantment, MinMaxBounds.IntBound.atLeast(level)));
            }
            predicates[i] = ItemPredicate.Builder.create().item(item.getItem()).nbt(item.getTag()).build();
            i++;
        }
        return predicates;
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.data.gen;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for (RegistryObject<NeedleItem> registry : RegistryHandler.NEEDLE_ITEMS) {
            NeedleItem item = registry.get();
            Ingredient material = item.getTier().getRepairMaterial();
            ShapedRecipeBuilder.shapedRecipe(item)
                    .patternLine("x  ")
                    .patternLine(" x ")
                    .patternLine("  y")
                    .key('x', material)
                    .key('y', Items.IRON_INGOT)
                    .addCriterion("has_material", hasItem(getPredicates(material)))
                    .build(consumer);
        }
        for (RegistryObject<HookItem> registry : RegistryHandler.HOOK_ITEMS) {
            HookItem item = registry.get();
            Ingredient material = item.getTier().getRepairMaterial();
            ShapedRecipeBuilder.shapedRecipe(item)
                    .patternLine("xxx")
                    .patternLine("x x")
                    .patternLine("x  ")
                    .key('x', material)
                    .addCriterion("has_material", hasItem(getPredicates(material)))
                    .build(consumer);
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

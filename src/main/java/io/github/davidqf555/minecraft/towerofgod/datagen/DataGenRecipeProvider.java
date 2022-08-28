package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DataGenRecipeProvider extends RecipeProvider {

    public DataGenRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        for (RegistryObject<NeedleItem> registry : ItemRegistry.NEEDLE_ITEMS) {
            NeedleItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                UpgradeRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_NEEDLE.get()), material, item)
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
        for (RegistryObject<HookItem> registry : ItemRegistry.HOOK_ITEMS) {
            HookItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                UpgradeRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_HOOK.get()), material, item)
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
        for (RegistryObject<SpearItem> registry : ItemRegistry.SPEARS) {
            SpearItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                UpgradeRecipeBuilder.smithing(Ingredient.of(ItemRegistry.DIAMOND_SPEAR.get()), material, item)
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
        Set<TagKey<Item>> tags = new HashSet<>();
        Set<ItemStack> items = new HashSet<>();
        for (Ingredient.Value list : ingredient.values) {
            if (list instanceof Ingredient.TagValue) {
                tags.add(((Ingredient.TagValue) list).tag);
            } else {
                items.addAll(list.getItems());
            }
        }
        ItemPredicate[] predicates = new ItemPredicate[tags.size() + items.size()];
        int i = 0;
        for (TagKey<Item> tag : tags) {
            predicates[i] = ItemPredicate.Builder.item().of(tag).build();
            i++;
        }
        for (ItemStack item : items) {
            ItemPredicate.Builder builder = ItemPredicate.Builder.item().of(item.getItem());
            CompoundTag nbt = item.getTag();
            if (nbt != null) {
                builder.hasNbt(nbt);
            }
            for (Tag tag : item.getEnchantmentTags()) {
                Enchantment enchantment = Enchantment.byId(((CompoundTag) tag).getInt("id"));
                int level = ((CompoundTag) tag).getInt("lvl");
                builder.hasEnchantment(new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.atLeast(level)));
            }
            predicates[i] = ItemPredicate.Builder.item().of(item.getItem()).hasNbt(item.getTag()).build();
            i++;
        }
        return predicates;
    }
}

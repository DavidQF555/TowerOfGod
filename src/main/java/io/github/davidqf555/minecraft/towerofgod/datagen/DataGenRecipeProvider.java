package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DataGenRecipeProvider extends RecipeProvider {

    public DataGenRecipeProvider(PackOutput generator) {
        super(generator);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        for (RegistryObject<NeedleItem> registry : ItemRegistry.NEEDLE_ITEMS) {
            NeedleItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(ItemRegistry.DIAMOND_NEEDLE.get()), material, RecipeCategory.COMBAT, item)
                        .unlocks("has_material", getTrigger(material))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, item)
                        .pattern("x  ")
                        .pattern(" x ")
                        .pattern("  y")
                        .define('x', material)
                        .define('y', Items.IRON_INGOT)
                        .unlockedBy("has_material", getTrigger(material))
                        .save(consumer, registry.getId());
            }
        }
        for (RegistryObject<? extends HookItem> registry : ItemRegistry.CRAFTABLE_HOOKS) {
            HookItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(ItemRegistry.DIAMOND_HOOK.get()), material, RecipeCategory.COMBAT, item)
                        .unlocks("has_material", getTrigger(material))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, item)
                        .pattern("xxx")
                        .pattern("x x")
                        .pattern("x  ")
                        .define('x', material)
                        .unlockedBy("has_material", getTrigger(material))
                        .save(consumer, registry.getId());
            }
        }
        for (RegistryObject<? extends SpearItem> registry : ItemRegistry.CRAFTABLE_SPEARS) {
            SpearItem item = registry.get();
            Tier tier = item.getTier();
            Ingredient material = tier.getRepairIngredient();
            if (tier.equals(Tiers.NETHERITE)) {
                SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(ItemRegistry.DIAMOND_SPEAR.get()), material, RecipeCategory.COMBAT, item)
                        .unlocks("has_material", getTrigger(material))
                        .save(consumer, registry.getId());

            } else {
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, item)
                        .pattern(" xx")
                        .pattern(" yx")
                        .pattern("y  ")
                        .define('x', material)
                        .define('y', Items.STICK)
                        .unlockedBy("has_material", getTrigger(material))
                        .save(consumer, registry.getId());
            }
        }
    }

    private Criterion<InventoryChangeTrigger.TriggerInstance> getTrigger(Ingredient ingredient) {
        ItemPredicate[] predicates = getPredicates(ingredient);
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, List.of(predicates)));
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

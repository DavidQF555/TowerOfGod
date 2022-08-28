package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public enum ModToolTier implements Tier {

    SHINSU(1000, 12, 2, 3, 0, () -> Ingredient.EMPTY),
    SUSPENDIUM(1250, 14, 1, 2, 15, () -> Ingredient.of(ItemRegistry.SUSPENDIUM.get()));

    private final int uses;
    private final float efficiency;
    private final float damage;
    private final int harvest;
    private final int enchantability;
    private final Supplier<Ingredient> repair;

    ModToolTier(int uses, float efficiency, float damage, int harvest, int enchantability, Supplier<Ingredient> repair) {
        this.uses = uses;
        this.efficiency = efficiency;
        this.damage = damage;
        this.harvest = harvest;
        this.enchantability = enchantability;
        this.repair = repair;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    @Override
    public int getLevel() {
        return harvest;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Nonnull
    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }

}

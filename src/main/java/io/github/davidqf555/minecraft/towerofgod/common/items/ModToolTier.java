package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;

public enum ModToolTier implements IItemTier {

    SHINSU(1000, 12, 2, 3, 0, Ingredient.EMPTY),
    SUSPENDIUM(1250, 14, 1, 2, 15, Ingredient.fromItems(RegistryHandler.SUSPENDIUM.get()));

    private final int uses;
    private final float efficiency;
    private final float damage;
    private final int harvest;
    private final int enchantability;
    private final Ingredient repair;

    ModToolTier(int uses, float efficiency, float damage, int harvest, int enchantability, Ingredient repair) {
        this.uses = uses;
        this.efficiency = efficiency;
        this.damage = damage;
        this.harvest = harvest;
        this.enchantability = enchantability;
        this.repair = repair;
    }

    @Override
    public int getMaxUses() {
        return uses;
    }

    @Override
    public float getEfficiency() {
        return efficiency;
    }

    @Override
    public float getAttackDamage() {
        return damage;
    }

    @Override
    public int getHarvestLevel() {
        return harvest;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return repair;
    }

}

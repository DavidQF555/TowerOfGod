package com.davidqf.minecraft.towerofgod.common.items;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.Set;

public class NeedleItem extends ToolItem {

    private static final Set<Block> EFFECTIVE = Sets.newHashSet();

    public NeedleItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, EFFECTIVE, properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) && (enchantment.type == EnchantmentType.WEAPON || enchantment.type == EnchantmentType.BREAKABLE || enchantment.type == EnchantmentType.VANISHABLE);
    }
}

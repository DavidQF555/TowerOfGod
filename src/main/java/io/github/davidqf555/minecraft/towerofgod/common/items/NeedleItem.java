package io.github.davidqf555.minecraft.towerofgod.common.items;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.HashSet;
import java.util.Set;

public class NeedleItem extends ToolItem {

    private static final Set<Block> EFFECTIVE = new HashSet<>();

    public NeedleItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, EFFECTIVE, properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.WEAPON || enchantment.type == EnchantmentType.BREAKABLE || enchantment.type == EnchantmentType.VANISHABLE;
    }
}

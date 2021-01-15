package com.davidqf.minecraft.towerofgod.common.items;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.Set;

public class HookItem extends ToolItem {

    private static final Set<Block> EFFECTIVE = Sets.newHashSet();

    public HookItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, EFFECTIVE, properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.WEAPON || enchantment.type == EnchantmentType.BREAKABLE || enchantment.type == EnchantmentType.VANISHABLE;
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        float mul = attacker.getDistance(target) / 4;
        Vector3d vel = attacker.getPositionVec().subtract(target.getPositionVec()).normalize().mul(mul, mul, mul);
        target.addVelocity(vel.getX(), vel.getY(), vel.getZ());
        return super.hitEntity(stack, target, attacker);
    }
}

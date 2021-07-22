package io.github.davidqf555.minecraft.towerofgod.common.items;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class HookItem extends ToolItem {

    private static final Set<Block> EFFECTIVE = new HashSet<>();

    public HookItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, EFFECTIVE, properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type == EnchantmentType.WEAPON || enchantment.type == EnchantmentType.BREAKABLE || enchantment.type == EnchantmentType.VANISHABLE;
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        float scale = attacker.getDistance(target) / 4;
        Vector3d vel = attacker.getPositionVec().subtract(target.getPositionVec()).normalize().scale(scale);
        target.addVelocity(vel.getX(), vel.getY(), vel.getZ());
        return super.hitEntity(stack, target, attacker);
    }
}

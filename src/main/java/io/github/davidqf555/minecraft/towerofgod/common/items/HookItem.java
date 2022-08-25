package io.github.davidqf555.minecraft.towerofgod.common.items;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;

@ParametersAreNonnullByDefault
public class HookItem extends ToolItem {

    public HookItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, new HashSet<>(), properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentType.WEAPON || enchantment.category == EnchantmentType.BREAKABLE || enchantment.category == EnchantmentType.VANISHABLE;
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        float scale = attacker.distanceTo(target) / 4;
        Vector3d vel = attacker.position().subtract(target.position()).normalize().scale(scale);
        target.push(vel.x(), vel.y(), vel.z());
        return super.hurtEnemy(stack, target, attacker);
    }
}

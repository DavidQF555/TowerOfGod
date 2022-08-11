package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
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

    public HookItem(IItemTier tier, float attackDamage, float attackSpeed) {
        super(attackDamage, attackSpeed, tier, new HashSet<>(), new Properties().group(TowerOfGod.TAB));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
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

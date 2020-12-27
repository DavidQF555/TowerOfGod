package com.davidqf.minecraft.towerofgod.common.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class HookItem extends SwordItem {

    public HookItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties prop) {
        super(tier, attackDamageIn, attackSpeedIn, prop);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        if (stack.getItem() instanceof HookItem) {
            float mul = attacker.getDistance(target) / 4;
            Vector3d vel = attacker.getPositionVec().subtract(target.getPositionVec()).normalize().mul(mul, mul, mul);
            target.addVelocity(vel.getX(), vel.getY(), vel.getZ());
        }
        return super.hitEntity(stack, target, attacker);
    }
}

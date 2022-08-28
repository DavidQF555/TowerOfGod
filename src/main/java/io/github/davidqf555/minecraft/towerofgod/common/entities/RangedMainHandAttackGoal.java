package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.EnumSet;
import java.util.Random;

public class RangedMainHandAttackGoal<T extends Mob & RangedAttackMob> extends Goal {

    private final T entity;
    private final double moveSpeedAmp;
    private final int attackCooldown;
    private final float maxAttackDistanceSq;
    private int attackTime;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime;

    public RangedMainHandAttackGoal(T mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
        entity = mob;
        moveSpeedAmp = moveSpeedAmpIn;
        attackCooldown = attackCooldownIn;
        maxAttackDistanceSq = maxAttackDistanceIn * maxAttackDistanceIn;
        attackTime = -1;
        strafingTime = -1;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.isAlive() && entity.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public boolean canContinueToUse() {
        return (canUse() || !entity.getNavigation().isDone()) && entity.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public void start() {
        super.start();
        entity.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        entity.setAggressive(false);
        seeTime = 0;
        attackTime = -1;
        entity.stopUsingItem();
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            double distSq = entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean canSee = entity.getSensing().hasLineOfSight(target);
            boolean positive = seeTime > 0;
            if (canSee != positive) {
                seeTime = 0;
            }
            if (canSee) {
                seeTime++;
            } else {
                seeTime--;
            }
            if (distSq <= maxAttackDistanceSq && seeTime >= 20) {
                entity.getNavigation().stop();
                ++strafingTime;
            } else {
                entity.getNavigation().moveTo(target, moveSpeedAmp);
                strafingTime = -1;
            }
            if (strafingTime >= 20) {
                Random rand = entity.getRandom();
                if (rand.nextFloat() < 0.3) {
                    strafingClockwise = !strafingClockwise;
                }
                if (rand.nextFloat() < 0.3) {
                    strafingBackwards = !strafingBackwards;
                }
                strafingTime = 0;
            }
            if (strafingTime > -1) {
                if (distSq > maxAttackDistanceSq * 0.75) {
                    strafingBackwards = false;
                } else if (distSq < maxAttackDistanceSq * 0.25) {
                    strafingBackwards = true;
                }
                entity.getMoveControl().strafe(strafingBackwards ? -0.5f : 0.5f, strafingClockwise ? 0.5f : -0.5f);
                entity.lookAt(target, 30, 30);
            } else {
                entity.getLookControl().setLookAt(target, 30, 30);
            }
            if (entity.isUsingItem()) {
                if (!canSee && seeTime < -60) {
                    entity.stopUsingItem();
                } else if (canSee) {
                    int count = entity.getTicksUsingItem();
                    if (count >= 20) {
                        entity.stopUsingItem();
                        entity.performRangedAttack(target, BowItem.getPowerForTime(count));
                        attackTime = attackCooldown;
                    }
                }
            } else if (--attackTime <= 0 && seeTime >= -60) {
                entity.startUsingItem(InteractionHand.MAIN_HAND);
            }
        }
    }
}

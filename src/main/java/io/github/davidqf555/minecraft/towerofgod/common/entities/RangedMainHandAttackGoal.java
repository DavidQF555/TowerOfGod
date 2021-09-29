package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BowItem;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.Hand;

import java.util.EnumSet;
import java.util.Random;

public class RangedMainHandAttackGoal<T extends MobEntity & IRangedAttackMob> extends Goal {

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
        setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() != null && entity.isAlive() && entity.getHeldItemMainhand().getItem() instanceof ShootableItem;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (shouldExecute() || !entity.getNavigator().noPath()) && entity.getHeldItemMainhand().getItem() instanceof ShootableItem;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        entity.setAggroed(true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        entity.setAggroed(false);
        seeTime = 0;
        attackTime = -1;
        entity.resetActiveHand();
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getAttackTarget();
        if (target != null) {
            double distSq = entity.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
            boolean canSee = entity.getEntitySenses().canSee(target);
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
                entity.getNavigator().clearPath();
                ++strafingTime;
            } else {
                entity.getNavigator().tryMoveToEntityLiving(target, moveSpeedAmp);
                strafingTime = -1;
            }
            if (strafingTime >= 20) {
                Random rand = entity.getRNG();
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
                entity.getMoveHelper().strafe(strafingBackwards ? -0.5f : 0.5f, strafingClockwise ? 0.5f : -0.5f);
                entity.faceEntity(target, 30, 30);
            } else {
                entity.getLookController().setLookPositionWithEntity(target, 30, 30);
            }
            if (entity.isHandActive()) {
                if (!canSee && seeTime < -60) {
                    entity.resetActiveHand();
                } else if (canSee) {
                    int count = entity.getItemInUseMaxCount();
                    if (count >= 20) {
                        entity.resetActiveHand();
                        entity.attackEntityWithRangedAttack(target, BowItem.getArrowVelocity(count));
                        attackTime = attackCooldown;
                    }
                }
            } else if (--attackTime <= 0 && seeTime >= -60) {
                entity.setActiveHand(Hand.MAIN_HAND);
            }
        }
    }
}

package com.davidqf.minecraft.towerofgod.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public abstract class FlyingDevice extends FlyingEntity {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".flyingdevice";
    private UUID owner;

    public FlyingDevice(EntityType<? extends FlyingDevice> type, World worldIn, @Nullable LivingEntity owner) {
        super(type, worldIn);
        moveController = new FlyingMovementController(this, 180, true);
        setPathPriority(PathNodeType.LAVA, -3);
        setPathPriority(PathNodeType.DAMAGE_FIRE, -2);
        setPathPriority(PathNodeType.DAMAGE_CACTUS, -2);
        setPathPriority(PathNodeType.DAMAGE_OTHER, -2);
        this.owner = owner == null ? null : owner.getUniqueID();
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FollowOwnerGoal());
    }

    @Nonnull
    @Override
    protected PathNavigator createNavigator(@Nonnull World worldIn) {
        return new FlyingPathNavigator(this, worldIn);
    }

    @Nullable
    public Entity getOwner() {
        if (owner != null && isServerWorld()) {
            return ((ServerWorld) world).getEntityByUuid(owner);
        }
        return null;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void travel(@Nonnull Vector3d vec) {
        float speed = (float) func_233637_b_(Attributes.field_233822_e_);
        setAIMoveSpeed(speed);
        if (isInWater()) {
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(0.8F));
        } else if (isInLava()) {
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(0.5D));
        } else {
            float f = 0.91F;
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(f));
        }
        func_233629_a_(this, false);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_INT_ARRAY)) {
            owner = nbt.getUniqueId(TAG_KEY);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (owner != null) {
            nbt.putUniqueId(TAG_KEY, owner);
        }
    }

    private class FollowOwnerGoal extends Goal {

        private static final double DISTANCE = 5;

        private FollowOwnerGoal() {
            setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            Entity owner = getOwner();
            return owner != null && !owner.isSpectator() && getDistanceSq(owner) >= DISTANCE * DISTANCE;
        }

        public void resetTask() {
            getNavigator().clearPath();
        }

        public void tick() {
            if (getOwner() != null) {
                getNavigator().tryMoveToXYZ(getOwner().getPosX(), getOwner().getPosYEye(), getOwner().getPosZ(), 1);
            }
        }
    }
}

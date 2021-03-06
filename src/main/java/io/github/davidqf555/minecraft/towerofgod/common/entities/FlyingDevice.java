package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IFlyingAnimal;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.UUID;

@ParametersAreNonnullByDefault
public abstract class FlyingDevice extends FlyingEntity implements IFlyingAnimal {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".flying_device";
    private UUID owner;

    public FlyingDevice(EntityType<? extends FlyingDevice> type, World worldIn) {
        super(type, worldIn);
        moveController = new FlyingMovementController(this, 180, true);
        setPathPriority(PathNodeType.LAVA, -1);
        setPathPriority(PathNodeType.DAMAGE_FIRE, -1);
        setPathPriority(PathNodeType.DAMAGE_CACTUS, -1);
        setPathPriority(PathNodeType.DAMAGE_OTHER, -1);
        owner = null;
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FollowOwnerGoal());
    }

    @Nonnull
    @Override
    public PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator nav = new FlyingPathNavigator(this, worldIn);
        nav.setCanEnterDoors(true);
        nav.setCanOpenDoors(false);
        nav.setCanSwim(true);
        return nav;
    }

    @Nullable
    public Entity getOwner() {
        if (owner != null && isServerWorld()) {
            return ((ServerWorld) world).getEntityByUuid(owner);
        }
        return null;
    }

    public void setOwnerID(UUID id) {
        owner = id;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void travel(Vector3d vec) {
        float speed = (float) getAttributeValue(Attributes.FLYING_SPEED);
        if (isInWater()) {
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(0.8));
        } else if (isInLava()) {
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(0.5));
        } else {
            double factor = 0.91;
            moveRelative(speed, vec);
            move(MoverType.SELF, getMotion());
            setMotion(getMotion().scale(factor));
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

        @Override
        public void resetTask() {
            getNavigator().clearPath();
        }

        @Override
        public void tick() {
            if (getOwner() != null) {
                getNavigator().tryMoveToXYZ(getOwner().getPosX(), getOwner().getPosYEye(), getOwner().getPosZ(), 1);
            }
        }
    }
}

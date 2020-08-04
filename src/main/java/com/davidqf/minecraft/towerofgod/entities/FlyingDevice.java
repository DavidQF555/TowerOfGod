package com.davidqf.minecraft.towerofgod.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class FlyingDevice extends FlyingEntity {

    private final IData data;

    public FlyingDevice(EntityType<? extends FlyingDevice> type, World worldIn, @Nullable LivingEntity owner) {
        super(type, worldIn);
        data = getCapability(DataProvider.capability).orElseThrow(NullPointerException::new);
        if(owner != null) {
            data.setOwner(owner.getUniqueID());
        }
        else {
            data.setOwner(null);
        }
        moveController = new FlyingMovementController(this, 180, true);
        setPathPriority(PathNodeType.LAVA, -3);
        setPathPriority(PathNodeType.DAMAGE_FIRE, -2);
        setPathPriority(PathNodeType.DAMAGE_CACTUS, -2);
        setPathPriority(PathNodeType.DAMAGE_OTHER, -2);
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
        if (data.getOwner() != null && isServerWorld()) {
            return ((ServerWorld) world).getEntityByUuid(data.getOwner());
        }
        return null;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
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

    public interface IData {

        UUID getOwner();

        void setOwner(UUID id);

    }

    public static class Data implements IData {

        private UUID owner;

        public Data() {
            owner = null;
        }

        @Override
        public UUID getOwner() {
            return owner;
        }

        @Override
        public void setOwner(UUID id) {
            owner = id;
        }

        public static class Factory implements Callable<IData> {
            @Override
            public IData call() {
                return new Data();
            }
        }
    }

    public static class DataProvider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(IData.class)
        public static Capability<IData> capability = null;
        private final LazyOptional<IData> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return cap == capability ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return capability.getStorage().writeNBT(capability, instance.orElseThrow(NullPointerException::new), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            capability.getStorage().readNBT(capability, instance.orElseThrow(NullPointerException::new), null, nbt);
        }
    }

    public static class DataStorage implements Capability.IStorage<IData> {

        @Override
        public INBT writeNBT(Capability<IData> capability, IData instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("owner", instance.getOwner());
            return tag;
        }

        @Override
        public void readNBT(Capability<IData> capability, IData instance, Direction side, INBT nbt) {
            CompoundNBT tag = (CompoundNBT) nbt;
            instance.setOwner(tag.getUniqueId("owner"));
        }
    }
}

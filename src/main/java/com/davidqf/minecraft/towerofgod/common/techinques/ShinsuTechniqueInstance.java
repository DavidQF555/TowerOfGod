package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechniqueInstance implements INBTSerializable<CompoundNBT> {

    private ShinsuTechnique technique;
    private UUID user;
    private int ticksLeft;
    private int level;

    public ShinsuTechniqueInstance(ShinsuTechnique technique, LivingEntity user, int level, int ticksLeft) {
        this.technique = technique;
        this.user = user == null ? null : user.getUniqueID();
        this.level = level;
        this.ticksLeft = ticksLeft;
    }

    public static double getTotalResistance(LivingEntity user, LivingEntity target) {
        IShinsuStats targetStats = IShinsuStats.get(target);
        IShinsuStats userStats = IShinsuStats.get(user);
        return targetStats.getResistance() / userStats.getTension();
    }

    public int ticksLeft() {
        return ticksLeft;
    }

    @Nullable
    public Entity getUser(World world) {
        if (world instanceof ServerWorld) {
            return ((ServerWorld) world).getEntityByUuid(user);
        }
        return null;
    }

    public ShinsuTechnique getTechnique() {
        return technique;
    }

    public int getLevel() {
        return level;
    }

    public void onEnd(World world) {
    }

    public void onUse(World world) {
    }

    public int getCooldown() {
        return 0;
    }

    public void remove(World world) {
        Entity user = getUser(world);
        if (user != null) {
            IShinsuStats stats = IShinsuStats.get(user);
            stats.removeTechnique(this);
        }
    }

    public void tick(World world) {
        ticksLeft--;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (user != null) {
            nbt.putUniqueId("User", user);
        }
        nbt.putString("Technique", technique.getName().getKey());
        nbt.putInt("Ticks", ticksLeft);
        nbt.putInt("Level", level);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("User")) {
            user = nbt.getUniqueId("User");
        }
        technique = ShinsuTechnique.get(nbt.getString("Technique"));
        ticksLeft = nbt.getInt("Ticks");
        level = nbt.getInt("Level");
    }

    public static abstract class Targetable extends ShinsuTechniqueInstance {

        private UUID target;

        public Targetable(ShinsuTechnique technique, LivingEntity user, int level, Entity target, int ticksLeft) {
            super(technique, user, level, ticksLeft);
            this.target = target == null ? null : target.getUniqueID();
        }

        public Entity getTarget(World world) {
            if (world instanceof ServerWorld) {
                return ((ServerWorld) world).getEntityByUuid(target);
            }
            return null;
        }

        public UUID getTargetUUID() {
            return target;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            nbt.putUniqueId("Target", target);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            target = nbt.getUniqueId("Target");
        }

    }

    public static abstract class Direction extends ShinsuTechniqueInstance {

        private Vector3d dir;

        public Direction(ShinsuTechnique technique, LivingEntity user, int level, Vector3d dir, int ticksLeft) {
            super(technique, user, level, ticksLeft);
            this.dir = dir;
        }

        public Vector3d getDirection() {
            return dir;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            nbt.putDouble("X", dir.getX());
            nbt.putDouble("Y", dir.getY());
            nbt.putDouble("Z", dir.getZ());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            dir = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }
}

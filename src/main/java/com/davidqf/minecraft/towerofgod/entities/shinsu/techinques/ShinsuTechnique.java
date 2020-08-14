package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechnique implements INBTSerializable<CompoundNBT> {

    private ShinsuTechniques technique;
    private UUID user;
    private int ticksLeft;
    private int level;

    public ShinsuTechnique(ShinsuTechniques technique, @Nullable LivingEntity user, int level) {
        this.technique = technique;
        this.user = user == null ? null : user.getUniqueID();
        this.level = level;
        ticksLeft = technique.getDuration();
    }

    public static double getTotalResistance(LivingEntity user, LivingEntity target) {
        double resistance = 1;
        ShinsuUser.IStats targetStats = ShinsuUser.getStats(target);
        ShinsuUser.IStats userStats = ShinsuUser.getStats(user);
        resistance *= targetStats.getResistance() * userStats.getTension();
        return resistance;
    }

    public boolean canUse(World world) {
        Entity e = getUser(world);
        if (e != null) {
            ShinsuUser.IStats stats = ShinsuUser.getStats(e);
            return stats.getShinsu() >= technique.getShinsuUse() && stats.getBaangs() >= technique.getBaangUse();
        }
        return false;
    }

    public int ticksLeft() {
        return ticksLeft;
    }

    public boolean isIdeal(World world) {
        return true;
    }

    @Nullable
    public Entity getUser(World world) {
        if (world instanceof ServerWorld) {
            return ((ServerWorld) world).getEntityByUuid(user);
        }
        return null;
    }

    public int getLevel() {
        return level;
    }

    public ShinsuTechniques getTechnique() {
        return technique;
    }

    public void onEnd(World world) {
        Entity e = getUser(world);
        if (e != null) {
            ShinsuUser.IStats stats = ShinsuUser.getStats(e);
            stats.setBaangs(stats.getBaangs() + technique.getBaangUse());
            stats.setShinsu(stats.getShinsu() + technique.getShinsuUse());
        }
    }

    public void onUse(World world) {
    }

    public void tick(World world) {
        ticksLeft--;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("technique", technique.name());
        nbt.putUniqueId("user", user);
        nbt.putInt("ticks", ticksLeft);
        nbt.putInt("level", level);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        technique = ShinsuTechniques.get(nbt.getString("technique"));
        user = nbt.getUniqueId("user");
        ticksLeft = nbt.getInt("ticks");
        level = nbt.getInt("level");
    }

    public static abstract class Targetable extends ShinsuTechnique {

        private UUID target;

        public Targetable(ShinsuTechniques technique, LivingEntity user, int level, LivingEntity target) {
            super(technique, user, level);
            this.target = target.getUniqueID();
        }

        public Entity getTarget(World world) {
            if (world instanceof ServerWorld) {
                return ((ServerWorld) world).getEntityByUuid(target);
            }
            return null;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            nbt.putUniqueId("target", target);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            target = nbt.getUniqueId("target");
        }

    }

    public static abstract class Position extends ShinsuTechnique {

        private Vector3d pos;

        public Position(ShinsuTechniques technique, LivingEntity user, int level, Vector3d pos) {
            super(technique, user, level);
            this.pos = pos;
        }

        public Vector3d getPosition() {
            return pos;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            nbt.putDouble("x", pos.getX());
            nbt.putDouble("y", pos.getY());
            nbt.putDouble("z", pos.getZ());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            pos = new Vector3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        }
    }
}

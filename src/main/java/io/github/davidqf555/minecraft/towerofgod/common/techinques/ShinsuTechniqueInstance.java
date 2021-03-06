package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateStatsMetersMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechniqueInstance implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private ShinsuTechnique technique;
    private UUID user;
    private int level;
    private int duration;
    private int ticks;

    public ShinsuTechniqueInstance(ShinsuTechnique technique, LivingEntity user, int level, int duration) {
        id = UUID.randomUUID();
        this.technique = technique;
        this.user = user == null ? null : user.getUniqueID();
        this.level = level;
        this.duration = duration;
        ticks = 0;
    }

    @Nullable
    public static ShinsuTechniqueInstance get(Entity user, UUID id) {
        IShinsuStats stats = IShinsuStats.get(user);
        for (ShinsuTechniqueInstance instance : stats.getTechniques()) {
            if (instance.id.equals(id)) {
                return instance;
            }
        }
        return null;
    }

    public UUID getID() {
        return id;
    }

    public int ticksLeft() {
        return duration - ticks;
    }

    public int getDuration() {
        return duration;
    }

    public int getTicks() {
        return ticks;
    }

    @Nullable
    public Entity getUser(ServerWorld world) {
        return world.getEntityByUuid(user);
    }

    public ShinsuTechnique getTechnique() {
        return technique;
    }

    public int getLevel() {
        return level;
    }

    public void onEnd(ServerWorld world) {

    }

    public void onUse(ServerWorld world) {
        updateMeter(world);
    }

    public int getCooldown() {
        return 0;
    }

    public void remove(ServerWorld world) {
        onEnd(world);
        Entity user = getUser(world);
        if (user != null) {
            IShinsuStats stats = IShinsuStats.get(user);
            stats.removeTechnique(this);
            updateMeter(world);
        }
    }

    public void tick(ServerWorld world) {
        ticks++;
    }

    private void updateMeter(ServerWorld world) {
        Entity user = getUser(world);
        if (user instanceof ServerPlayerEntity) {
            IShinsuStats stats = IShinsuStats.get(user);
            UpdateStatsMetersMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("ID", id);
        if (user != null) {
            nbt.putUniqueId("User", user);
        }
        nbt.putString("Technique", technique.getName());
        nbt.putInt("Duration", duration);
        nbt.putInt("Ticks", ticks);
        nbt.putInt("Level", level);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        id = nbt.getUniqueId("ID");
        user = nbt.getUniqueId("User");
        technique = ShinsuTechnique.get(nbt.getString("Technique"));
        duration = nbt.getInt("Duration");
        ticks = nbt.getInt("Ticks");
        level = nbt.getInt("Level");
    }

    public static abstract class Targetable extends ShinsuTechniqueInstance {

        private UUID target;

        public Targetable(ShinsuTechnique technique, LivingEntity user, int level, Entity target, int duration) {
            super(technique, user, level, duration);
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

        public Direction(ShinsuTechnique technique, LivingEntity user, int level, Vector3d dir, int duration) {
            super(technique, user, level, duration);
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

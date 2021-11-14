package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechniqueInstance implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private UUID user;
    private int level;
    private int duration;
    private int ticks;

    public ShinsuTechniqueInstance(LivingEntity user, int level) {
        id = UUID.randomUUID();
        this.user = user == null ? null : user.getUniqueID();
        this.level = level;
        ticks = 0;
        duration = getInitialDuration();
    }

    @Nullable
    public static ShinsuTechniqueInstance get(Entity user, UUID id) {
        ShinsuStats stats = ShinsuStats.get(user);
        for (ShinsuTechniqueInstance instance : stats.getTechniques()) {
            if (instance.id.equals(id)) {
                return instance;
            }
        }
        return null;
    }

    public int getInitialDuration() {
        return 1;
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

    @Nullable
    public Entity getUser(ServerWorld world) {
        return world.getEntityByUuid(user);
    }

    public abstract ShinsuTechnique getTechnique();

    public int getLevel() {
        return level;
    }

    public void onEnd(ServerWorld world) {
    }

    public void onUse(ServerWorld world) {
        updateMeters(world);
    }

    public int getCooldown() {
        return 0;
    }

    public abstract int getShinsuUse();

    public abstract int getBaangsUse();

    public void remove(ServerWorld world) {
        onEnd(world);
        Entity user = getUser(world);
        if (user != null) {
            ShinsuStats stats = ShinsuStats.get(user);
            stats.removeTechnique(this);
            updateMeters(world);
        }
    }

    public void tick(ServerWorld world) {
        if (!getTechnique().isIndefinite()) {
            ticks++;
        }
    }

    public boolean isConflicting(ShinsuTechniqueInstance instance) {
        ShinsuTechnique technique = getTechnique();
        return technique.getRepeatEffect() == ShinsuTechnique.Repeat.DENY && technique == instance.getTechnique();
    }

    protected void updateMeters(ServerWorld world) {
        Entity user = getUser(world);
        if (user instanceof ServerPlayerEntity) {
            ShinsuStats stats = ShinsuStats.get(user);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("ID", getID());
        if (user != null) {
            nbt.putUniqueId("User", user);
        }
        nbt.putString("Technique", getTechnique().name());
        nbt.putInt("Duration", getDuration());
        nbt.putInt("Ticks", ticks);
        nbt.putInt("Level", getLevel());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("ID", Constants.NBT.TAG_INT_ARRAY)) {
            id = nbt.getUniqueId("ID");
        }
        if (nbt.contains("User", Constants.NBT.TAG_INT_ARRAY)) {
            user = nbt.getUniqueId("User");
        }
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Ticks", Constants.NBT.TAG_INT)) {
            ticks = nbt.getInt("Ticks");
        }
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
    }

    public static abstract class Targetable extends ShinsuTechniqueInstance {

        private UUID target;

        public Targetable(LivingEntity user, int level, Entity target) {
            super(user, level);
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
            nbt.putUniqueId("Target", getTargetUUID());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            if (nbt.contains("Target", Constants.NBT.TAG_INT_ARRAY)) {
                target = nbt.getUniqueId("Target");
            }
        }

    }

    public static abstract class Direction extends ShinsuTechniqueInstance {

        private Vector3d dir;

        public Direction(LivingEntity user, int level, Vector3d dir) {
            super(user, level);
            this.dir = dir;
        }

        public Vector3d getDirection() {
            return dir;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = super.serializeNBT();
            ListNBT direction = new ListNBT();
            Vector3d dir = getDirection();
            direction.add(DoubleNBT.valueOf(dir.getX()));
            direction.add(DoubleNBT.valueOf(dir.getY()));
            direction.add(DoubleNBT.valueOf(dir.getZ()));
            nbt.put("Direction", direction);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            if (nbt.contains("Direction", Constants.NBT.TAG_LIST)) {
                ListNBT direction = nbt.getList("Direction", Constants.NBT.TAG_DOUBLE);
                dir = new Vector3d(direction.getDouble(0), direction.getDouble(1), direction.getDouble(2));
            }
        }
    }
}

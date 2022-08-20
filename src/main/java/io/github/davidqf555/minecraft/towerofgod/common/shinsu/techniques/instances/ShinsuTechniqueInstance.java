package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechniqueInstance implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private UUID user;
    private int ticks;

    public ShinsuTechniqueInstance(LivingEntity user) {
        id = MathHelper.getRandomUUID();
        this.user = user == null ? null : user.getUniqueID();
        ticks = 0;
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

    public UUID getID() {
        return id;
    }

    public int getTicks() {
        return ticks;
    }

    public int getDuration() {
        return 1;
    }

    @Nullable
    public Entity getUser(ServerWorld world) {
        return world.getEntityByUuid(user);
    }

    public abstract ShinsuTechnique getTechnique();

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

    public void periodicTick(ServerWorld world, int period) {
        if (!getTechnique().isIndefinite()) {
            ticks += period;
        }
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
        nbt.putString("Technique", getTechnique().getRegistryName().toString());
        nbt.putInt("Ticks", ticks);
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
        if (nbt.contains("Ticks", Constants.NBT.TAG_INT)) {
            ticks = nbt.getInt("Ticks");
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class ShinsuTechniqueInstance implements INBTSerializable<CompoundTag> {

    private UUID id;
    private UUID user;
    private int ticks;

    public ShinsuTechniqueInstance(LivingEntity user) {
        id = Mth.createInsecureUUID();
        this.user = user == null ? null : user.getUUID();
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
    public Entity getUser(ServerLevel world) {
        return world.getEntity(user);
    }

    public abstract ShinsuTechnique getTechnique();

    public void onEnd(ServerLevel world) {
    }

    public void onUse(ServerLevel world) {
        updateMeters(world);
    }

    public int getCooldown() {
        return 0;
    }

    public abstract int getShinsuUse();

    public abstract int getBaangsUse();

    public void remove(ServerLevel world) {
        onEnd(world);
        Entity user = getUser(world);
        if (user != null) {
            ShinsuStats stats = ShinsuStats.get(user);
            stats.removeTechnique(this);
            updateMeters(world);
        }
    }

    public void tick(ServerLevel world) {
        if (!getTechnique().isIndefinite()) {
            ticks++;
        }
    }

    protected void updateMeters(ServerLevel world) {
        Entity user = getUser(world);
        if (user instanceof ServerPlayer) {
            ShinsuStats stats = ShinsuStats.get(user);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) user), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) user), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("ID", getID());
        if (user != null) {
            nbt.putUUID("User", user);
        }
        nbt.putString("Technique", getTechnique().getRegistryName().toString());
        nbt.putInt("Ticks", ticks);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("ID", Tag.TAG_INT_ARRAY)) {
            id = nbt.getUUID("ID");
        }
        if (nbt.contains("User", Tag.TAG_INT_ARRAY)) {
            user = nbt.getUUID("User");
        }
        if (nbt.contains("Ticks", Tag.TAG_INT)) {
            ticks = nbt.getInt("Ticks");
        }
    }
}

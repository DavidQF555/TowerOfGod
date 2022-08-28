package io.github.davidqf555.minecraft.towerofgod.common.world;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegularTeamsSavedData extends SavedData {

    private static final String NAME = TowerOfGod.MOD_ID + "_RegularTeams";
    private static final double RANGE = 32;
    private final List<RegularTeam> teams;

    public RegularTeamsSavedData() {
        teams = new ArrayList<>();
    }

    public RegularTeamsSavedData(CompoundTag compound) {
        this();
        if (compound.contains("Teams", Tag.TAG_LIST)) {
            ListTag teams = compound.getList("Teams", Tag.TAG_COMPOUND);
            teams.forEach(nbt -> {
                RegularTeam team = new RegularTeam();
                team.deserializeNBT((CompoundTag) nbt);
                this.teams.add(team);
            });
        }
    }

    public static RegularTeamsSavedData getOrCreate(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(RegularTeamsSavedData::new, RegularTeamsSavedData::new, NAME);
    }

    public static RegularTeam getOrCreateTeam(ServerLevel world, RegularEntity entity) {
        RegularTeamsSavedData data = getOrCreate(world);
        UUID id = entity.getUUID();
        for (RegularTeam team : data.teams) {
            if (team.members.contains(id)) {
                return team;
            }
        }
        RegularTeam team = new RegularTeam();
        team.members.add(id);
        team.leader = id;
        data.teams.add(team);
        return team;
    }

    public void update(ServerLevel world) {
        for (int i = teams.size() - 1; i >= 0; i--) {
            RegularTeam team = teams.get(i);
            List<RegularEntity> entities = new ArrayList<>();
            RegularEntity max = null;
            int maxLevel = 0;
            for (int j = team.members.size() - 1; j >= 0; j--) {
                UUID id = team.members.get(j);
                Entity entity = world.getEntity(id);
                if (entity instanceof RegularEntity) {
                    entities.add((RegularEntity) entity);
                    int level = ShinsuStats.get(entity).getLevel();
                    if (level > maxLevel) {
                        max = (RegularEntity) entity;
                        maxLevel = level;
                    }
                } else {
                    team.members.remove(j);
                    if (id.equals(team.leader)) {
                        team.leader = null;
                    }
                }
            }
            if (max == null) {
                teams.remove(i);
            } else {
                team.leader = max.getUUID();
                for (RegularEntity entity : entities) {
                    if (!entity.equals(max) && entity.distanceToSqr(max) > RANGE * RANGE) {
                        team.members.remove(entity.getUUID());
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag teams = new ListTag();
        for (RegularTeam team : this.teams) {
            teams.add(team.serializeNBT());
        }
        compound.put("Teams", teams);
        return compound;
    }

    public static class RegularTeam implements INBTSerializable<CompoundTag> {

        private final List<UUID> members;
        private UUID leader;

        private RegularTeam() {
            members = new ArrayList<>();
        }

        public List<UUID> getMembers() {
            return members;
        }

        public UUID getLeader() {
            return leader;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            ListTag ids = new ListTag();
            for (UUID id : members) {
                ids.add(NbtUtils.createUUID(id));
            }
            nbt.put("Members", ids);
            if (leader != null) {
                nbt.put("Leader", NbtUtils.createUUID(leader));
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            if (nbt.contains("Members", Tag.TAG_LIST)) {
                ListTag ids = nbt.getList("Members", Tag.TAG_INT_ARRAY);
                ids.forEach(id -> members.add(NbtUtils.loadUUID(id)));
            }
            if (nbt.contains("Leader", Tag.TAG_INT_ARRAY)) {
                leader = nbt.getUUID("Leader");
            }
        }
    }
}

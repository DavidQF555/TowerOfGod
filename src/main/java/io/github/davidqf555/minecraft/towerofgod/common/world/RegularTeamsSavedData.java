package io.github.davidqf555.minecraft.towerofgod.common.world;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegularTeamsSavedData extends WorldSavedData {

    private static final String NAME = TowerOfGod.MOD_ID + "_RegularTeams";
    private static final double RANGE = 32;
    private final List<RegularTeam> teams;

    public RegularTeamsSavedData() {
        super(NAME);
        teams = new ArrayList<>();
    }

    public static RegularTeamsSavedData getOrCreate(ServerWorld world) {
        return world.getSavedData().getOrCreate(RegularTeamsSavedData::new, NAME);
    }

    public static RegularTeam getOrCreateTeam(ServerWorld world, RegularEntity entity) {
        RegularTeamsSavedData data = getOrCreate(world);
        UUID id = entity.getUniqueID();
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

    public void tick(ServerWorld world) {
        for (int i = teams.size() - 1; i >= 0; i--) {
            RegularTeam team = teams.get(i);
            List<RegularEntity> entities = new ArrayList<>();
            RegularEntity max = null;
            int maxLevel = 0;
            for (int j = team.members.size() - 1; j >= 0; j--) {
                UUID id = team.members.get(j);
                Entity entity = world.getEntityByUuid(id);
                if (entity instanceof RegularEntity) {
                    entities.add((RegularEntity) entity);
                    int level = ((RegularEntity) entity).getShinsuLevel();
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
                team.leader = max.getUniqueID();
                for (RegularEntity entity : entities) {
                    if (!entity.equals(max) && entity.getDistanceSq(max) > RANGE * RANGE) {
                        team.members.remove(entity.getUniqueID());
                    }
                }
            }
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("Teams", Constants.NBT.TAG_LIST)) {
            ListNBT teams = compound.getList("Teams", Constants.NBT.TAG_COMPOUND);
            teams.forEach(nbt -> {
                RegularTeam team = new RegularTeam();
                team.deserializeNBT((CompoundNBT) nbt);
                this.teams.add(team);
            });
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT teams = new ListNBT();
        for (RegularTeam team : this.teams) {
            teams.add(team.serializeNBT());
        }
        compound.put("Teams", teams);
        return compound;
    }

    public static class RegularTeam implements INBTSerializable<CompoundNBT> {

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
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT ids = new ListNBT();
            for (UUID id : members) {
                ids.add(NBTUtil.func_240626_a_(id));
            }
            nbt.put("Members", ids);
            if (leader != null) {
                nbt.put("Leader", NBTUtil.func_240626_a_(leader));
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            if (nbt.contains("Members", Constants.NBT.TAG_LIST)) {
                ListNBT ids = nbt.getList("Members", Constants.NBT.TAG_INT_ARRAY);
                ids.forEach(id -> members.add(NBTUtil.readUniqueId(id)));
            }
            if (nbt.contains("Leader", Constants.NBT.TAG_INT_ARRAY)) {
                leader = nbt.getUniqueId("Leader");
            }
        }
    }
}

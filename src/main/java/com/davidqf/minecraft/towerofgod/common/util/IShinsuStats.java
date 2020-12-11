package com.davidqf.minecraft.towerofgod.common.util;

import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancement;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancementProgress;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuStatsSyncMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.packets.CastShinsuMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

public interface IShinsuStats {

    @Nonnull
    static IShinsuStats get(Entity user) {
        return user.getCapability(StatsProvider.capability).orElseGet(ShinsuStats::new);
    }

    List<ShinsuTechnique> getTechniques();

    Map<ShinsuTechniques, Integer> getKnownTechniques();

    int getTechniqueLevel(ShinsuTechniques technique);

    int getShinsu();

    int getBaangs();

    double getResistance();

    double getTension();

    ShinsuQuality getQuality();

    void setQuality(ShinsuQuality quality);

    Map<ShinsuTechniques, Integer> getCooldowns();

    Map<ShinsuAdvancement, ShinsuAdvancementProgress> getAdvancements();

    void cast(LivingEntity user, ShinsuTechniques technique, @Nullable Entity target, @Nullable Vector3d pos);

    CompoundNBT serialize();

    void deserialize(CompoundNBT nbt);

    class ShinsuStats implements IShinsuStats {

        private ShinsuQuality quality;
        private final Map<ShinsuTechniques, Integer> cooldowns;
        private final Map<ShinsuAdvancement, ShinsuAdvancementProgress> advancements;
        private final List<ShinsuTechnique> techniques;

        public ShinsuStats() {
            this(ShinsuQuality.NONE, new EnumMap<>(ShinsuTechniques.class), new EnumMap<>(ShinsuAdvancement.class));
        }

        private ShinsuStats(ShinsuQuality quality, Map<ShinsuTechniques, Integer> cooldowns, Map<ShinsuAdvancement, ShinsuAdvancementProgress> advancements) {
            this.quality = quality;
            this.cooldowns = cooldowns;
            this.advancements = advancements;
            techniques = new ArrayList<>();
        }

        @Override
        public List<ShinsuTechnique> getTechniques() {
            return techniques;
        }

        @Override
        public Map<ShinsuTechniques, Integer> getKnownTechniques() {
            Map<ShinsuTechniques, Integer> known = new EnumMap<>(ShinsuTechniques.class);
            for (ShinsuAdvancementProgress progress : getAdvancements().values()) {
                if (progress.isComplete()) {
                    for (ShinsuTechniques technique : progress.getAdvancement().getReward().getTechniques()) {
                        if (known.containsKey(technique)) {
                            known.put(technique, known.get(technique) + 1);
                        } else {
                            known.put(technique, 1);
                        }
                    }
                }
            }
            return known;
        }

        @Override
        public int getTechniqueLevel(ShinsuTechniques technique) {
            Map<ShinsuTechniques, Integer> known = getKnownTechniques();
            return known.get(technique);
        }

        private int getMaxShinsu() {
            int amt = 0;
            for (ShinsuAdvancementProgress progress : advancements.values()) {
                if (progress.isComplete()) {
                    amt += progress.getAdvancement().getReward().getShinsu();
                }
            }
            return amt;
        }

        private int getMaxBaangs() {
            int amt = 0;
            for (ShinsuAdvancementProgress progress : advancements.values()) {
                if (progress.isComplete()) {
                    amt += progress.getAdvancement().getReward().getBaangs();
                }
            }
            return amt;
        }

        @Override
        public int getShinsu() {
            int inUse = 0;
            for (ShinsuTechnique technique : techniques) {
                inUse += ShinsuTechniques.get(technique).getShinsuUse();
            }
            return getMaxShinsu() - inUse;
        }

        @Override
        public int getBaangs() {
            int inUse = 0;
            for (ShinsuTechnique technique : techniques) {
                inUse += ShinsuTechniques.get(technique).getBaangUse();
            }
            return getMaxBaangs() - inUse;
        }

        @Override
        public double getResistance() {
            double amt = 1;
            for (ShinsuAdvancementProgress progress : advancements.values()) {
                if (progress.isComplete()) {
                    amt *= progress.getAdvancement().getReward().getResistance();
                }
            }
            return amt;
        }

        @Override
        public double getTension() {
            double amt = 1;
            for (ShinsuAdvancementProgress progress : advancements.values()) {
                if (progress.isComplete()) {
                    amt *= progress.getAdvancement().getReward().getTension();
                }
            }
            return amt;
        }

        @Override
        public ShinsuQuality getQuality() {
            return quality;
        }

        @Override
        public void setQuality(ShinsuQuality quality) {
            this.quality = quality;
        }

        @Override
        public Map<ShinsuTechniques, Integer> getCooldowns() {
            return cooldowns;
        }

        @Override
        public Map<ShinsuAdvancement, ShinsuAdvancementProgress> getAdvancements() {
            for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
                if (!advancements.containsKey(advancement)) {
                    advancements.put(advancement, new ShinsuAdvancementProgress(advancement, 0, false));
                }
            }
            return advancements;
        }

        @Override
        public void cast(LivingEntity user, ShinsuTechniques technique, @Nullable Entity target, @Nullable Vector3d dir) {
            if(user.world instanceof ServerWorld) {
                if (!onCooldown(technique)) {
                    int level = getTechniqueLevel(technique);
                    ShinsuTechniques.Builder<? extends ShinsuTechnique> builder = technique.getBuilder();
                    if (builder.canCast(technique, user, level, target, dir)) {
                        ShinsuTechnique tech = technique.getBuilder().build(user, level, target, dir);
                        if (tech != null) {
                            cooldowns.put(technique, tech.getCooldown());
                            techniques.add(tech);
                            tech.onUse(user.world);
                            if (user instanceof ServerPlayerEntity) {
                                ShinsuStatsSyncMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new ShinsuStatsSyncMessage(this));
                            }
                        }
                    }
                }
            }
            else{
                CastShinsuMessage.INSTANCE.sendToServer(new CastShinsuMessage(user.getUniqueID(), technique, target == null ? null : target.getUniqueID(), dir));
            }
        }

        private boolean onCooldown(ShinsuTechniques technique) {
            return cooldowns.containsKey(technique) && cooldowns.get(technique) > 0;
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            CompoundNBT list = new CompoundNBT();
            list.putInt("Size", techniques.size());
            for (int i = 0; i < techniques.size(); i++) {
                ShinsuTechnique tech = techniques.get(i);
                list.put(i + 1 + "", tech.serializeNBT());
                list.putString("Type" + (i + 1), ShinsuTechniques.get(tech).getName().getKey());
            }
            tag.put("Techniques", list);
            tag.putString("Quality", quality.name());
            CompoundNBT cool = new CompoundNBT();
            for (ShinsuTechniques tech : cooldowns.keySet()) {
                cool.putInt(tech.name(), cooldowns.get(tech));
            }
            tag.put("Cooldowns", cool);
            CompoundNBT adv = new CompoundNBT();
            for (ShinsuAdvancement advancement : getAdvancements().keySet()) {
                String key = advancement.getName().getKey();
                adv.put(key, advancements.get(advancement).serializeNBT());
            }
            tag.put("Advancements", adv);
            return tag;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {
            techniques.clear();
            CompoundNBT list = nbt.getCompound("Techniques");
            for (int i = 0; i < list.getInt("Size"); i++) {
                ShinsuTechniques type = ShinsuTechniques.get(list.getString("Type" + (i + 1)));
                ShinsuTechnique tech = type.getBuilder().emptyBuild();
                tech.deserializeNBT((CompoundNBT) list.get(i + 1 + ""));
                techniques.add(tech);
            }
            quality = ShinsuQuality.get(nbt.getString("Quality"));
            cooldowns.clear();
            CompoundNBT cool = nbt.getCompound("Cooldowns");
            for (ShinsuTechniques tech : ShinsuTechniques.values()) {
                String key = tech.name();
                cooldowns.put(tech, cool.getInt(key));
            }
            advancements.clear();
            CompoundNBT ad = nbt.getCompound("Advancements");
            for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
                String key = advancement.getName().getKey();
                ShinsuAdvancementProgress progress = new ShinsuAdvancementProgress(null, 0, false);
                progress.deserializeNBT((CompoundNBT) ad.get(key));
                advancements.put(advancement, progress);
            }
        }

        public static class Factory implements Callable<IShinsuStats> {
            @Override
            public IShinsuStats call() {
                return new ShinsuStats();
            }
        }
    }

    class StatsProvider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(IShinsuStats.class)
        public static Capability<IShinsuStats> capability = null;
        private final LazyOptional<IShinsuStats> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

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

    class StatsStorage implements Capability.IStorage<IShinsuStats> {

        @Override
        public INBT writeNBT(Capability<IShinsuStats> capability, IShinsuStats instance, Direction side) {
            return instance.serialize();
        }

        @Override
        public void readNBT(Capability<IShinsuStats> capability, IShinsuStats instance, Direction side, INBT nbt) {
            instance.deserialize((CompoundNBT) nbt);
        }
    }
}

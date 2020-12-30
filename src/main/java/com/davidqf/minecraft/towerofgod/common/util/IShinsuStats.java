package com.davidqf.minecraft.towerofgod.common.util;

import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancement;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancementProgress;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuStatsSyncMessage;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuTechniqueMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
import java.util.function.Supplier;

public interface IShinsuStats {

    @Nonnull
    static IShinsuStats get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(AdvancementShinsuStats::new);
    }

    Type getType();

    List<ShinsuTechniqueInstance> getTechniques();

    void addTechnique(ShinsuTechniqueInstance technique);

    void removeTechnique(ShinsuTechniqueInstance technique);

    int getTechniqueLevel(ShinsuTechnique technique);

    void addKnownTechnique(ShinsuTechnique technique, int level);

    int getShinsu();

    int getBaangs();

    double getResistance();

    double getTension();

    default ShinsuQuality getQuality() {
        return ShinsuQuality.NONE;
    }

    void setQuality(ShinsuQuality quality);

    int getCooldown(ShinsuTechnique technique);

    void addCooldown(ShinsuTechnique technique, int time);

    default void cast(LivingEntity user, ShinsuTechnique technique, @Nullable Entity target, @Nullable Vector3d dir) {
        if (getCooldown(technique) <= 0) {
            int level = getTechniqueLevel(technique);
            ShinsuTechnique.Builder<? extends ShinsuTechniqueInstance> builder = technique.getBuilder();
            if (builder.canCast(technique, user, level, target, dir)) {
                ShinsuTechniqueInstance tech = technique.getBuilder().build(user, level, target, dir);
                if (tech != null) {
                    addCooldown(technique, tech.getCooldown());
                    addTechnique(tech);
                    if (user.world instanceof ServerWorld) {
                        tech.onUse(user.world);
                        if (user instanceof ServerPlayerEntity) {
                            ShinsuStatsSyncMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new ShinsuStatsSyncMessage(this));
                        }
                    } else if (user instanceof ClientPlayerEntity) {
                        ShinsuTechniqueMessage.INSTANCE.sendToServer(new ShinsuTechniqueMessage(ShinsuTechniqueMessage.Action.USE, tech));
                        ShinsuStatsSyncMessage.INSTANCE.sendToServer(new ShinsuStatsSyncMessage(this));
                    }
                }
            }
        }
    }

    CompoundNBT serialize();

    void deserialize(CompoundNBT nbt);

    enum Type {

        ADVANCEMENT(AdvancementShinsuStats::new);

        private final Supplier<IShinsuStats> supplier;

        Type(Supplier<IShinsuStats> supplier) {
            this.supplier = supplier;
        }

        public static Type get(String name) {
            for (Type type : values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
            return null;
        }

        public Supplier<IShinsuStats> getSupplier() {
            return supplier;
        }
    }

    abstract class ShinsuStats implements IShinsuStats {

        private ShinsuQuality quality;
        private final Map<ShinsuTechnique, Integer> cooldowns;
        private final List<ShinsuTechniqueInstance> techniques;

        public ShinsuStats(ShinsuQuality quality, Map<ShinsuTechnique, Integer> cooldowns, List<ShinsuTechniqueInstance> techniques) {
            this.quality = quality;
            this.cooldowns = cooldowns;
            this.techniques = techniques;
        }

        @Override
        public List<ShinsuTechniqueInstance> getTechniques() {
            return techniques;
        }

        @Override
        public void addTechnique(ShinsuTechniqueInstance technique) {
            techniques.add(technique);
        }

        @Override
        public void removeTechnique(ShinsuTechniqueInstance technique) {
            techniques.remove(technique);
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
        public int getCooldown(ShinsuTechnique technique) {
            return cooldowns.getOrDefault(technique, 0);
        }

        @Override
        public void addCooldown(ShinsuTechnique technique, int time) {
            cooldowns.put(technique, time);
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            CompoundNBT list = new CompoundNBT();
            list.putInt("Size", techniques.size());
            for (int i = 0; i < techniques.size(); i++) {
                ShinsuTechniqueInstance tech = techniques.get(i);
                list.put(i + 1 + "", tech.serializeNBT());
                list.putString("Type" + (i + 1), ShinsuTechnique.get(tech).getName().getKey());
            }
            tag.put("Techniques", list);
            tag.putString("Quality", quality.name());
            CompoundNBT cool = new CompoundNBT();
            for (ShinsuTechnique tech : cooldowns.keySet()) {
                cool.putInt(tech.name(), cooldowns.get(tech));
            }
            tag.put("Cooldowns", cool);
            return tag;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {
            techniques.clear();
            CompoundNBT list = nbt.getCompound("Techniques");
            for (int i = 0; i < list.getInt("Size"); i++) {
                ShinsuTechnique type = ShinsuTechnique.get(list.getString("Type" + (i + 1)));
                ShinsuTechniqueInstance tech = type.getBuilder().emptyBuild();
                tech.deserializeNBT((CompoundNBT) list.get(i + 1 + ""));
                techniques.add(tech);
            }
            quality = ShinsuQuality.get(nbt.getString("Quality"));
            cooldowns.clear();
            CompoundNBT cool = nbt.getCompound("Cooldowns");
            for (ShinsuTechnique tech : ShinsuTechnique.values()) {
                String key = tech.name();
                cooldowns.put(tech, cool.getInt(key));
            }
        }

    }

    class AdvancementShinsuStats extends ShinsuStats {

        private final Map<ShinsuAdvancement, ShinsuAdvancementProgress> advancements;

        public AdvancementShinsuStats() {
            this(ShinsuQuality.NONE, new EnumMap<>(ShinsuTechnique.class), new EnumMap<>(ShinsuAdvancement.class));
        }

        private AdvancementShinsuStats(ShinsuQuality quality, Map<ShinsuTechnique, Integer> cooldowns, Map<ShinsuAdvancement, ShinsuAdvancementProgress> advancements) {
            super(quality, cooldowns, new ArrayList<>());
            this.advancements = advancements;
        }

        @Override
        public Type getType() {
            return Type.ADVANCEMENT;
        }

        @Override
        public int getTechniqueLevel(ShinsuTechnique technique) {
            int count = 0;
            for (ShinsuAdvancement advancement : advancements.keySet()) {
                for (ShinsuTechnique reward : advancement.getReward().getTechniques()) {
                    if (reward == technique) {
                        count++;
                        break;
                    }
                }
            }
            return count;
        }

        @Override
        public void addKnownTechnique(ShinsuTechnique technique, int level) {
        }

        public int getMaxShinsu() {
            int amt = 0;
            for (ShinsuAdvancementProgress progress : advancements.values()) {
                if (progress.isComplete()) {
                    amt += progress.getAdvancement().getReward().getShinsu();
                }
            }
            return amt;
        }

        public int getMaxBaangs() {
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
            for (ShinsuTechniqueInstance technique : getTechniques()) {
                inUse += ShinsuTechnique.get(technique).getShinsuUse();
            }
            return getMaxShinsu() - inUse;
        }

        @Override
        public int getBaangs() {
            int inUse = 0;
            for (ShinsuTechniqueInstance technique : getTechniques()) {
                inUse += ShinsuTechnique.get(technique).getBaangUse();
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

        public List<ShinsuAdvancement> getUnlockedAdvancements() {
            List<ShinsuAdvancement> advancements = new ArrayList<>();
            for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
                if (advancement.getParent() == null) {
                    addUnlockedAdvancements(advancements, advancement);
                }
            }
            return advancements;
        }

        private void addUnlockedAdvancements(List<ShinsuAdvancement> advancements, ShinsuAdvancement advancement) {
            Map<ShinsuAdvancement, ShinsuAdvancementProgress> progress = getAdvancements();
            if (progress.get(advancement).isComplete()) {
                for (ShinsuAdvancement ad : advancement.getDirectChildren()) {
                    addUnlockedAdvancements(advancements, ad);
                }
            } else {
                advancements.add(advancement);
            }
        }

        public Map<ShinsuAdvancement, ShinsuAdvancementProgress> getAdvancements() {
            for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
                if (!advancements.containsKey(advancement)) {
                    advancements.put(advancement, new ShinsuAdvancementProgress(advancement, 0, false));
                }
            }
            return advancements;
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT tag = super.serialize();
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
            super.deserialize(nbt);
            advancements.clear();
            CompoundNBT ad = nbt.getCompound("Advancements");
            for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
                String key = advancement.getName().getKey();
                ShinsuAdvancementProgress progress = new ShinsuAdvancementProgress(null, 0, false);
                progress.deserializeNBT((CompoundNBT) ad.get(key));
                advancements.put(advancement, progress);
            }
        }
    }

    class Provider implements ICapabilitySerializable<INBT> {

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

    class Storage implements Capability.IStorage<IShinsuStats> {

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

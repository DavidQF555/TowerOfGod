package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientKnownPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateInitialCooldownsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateStatsMetersPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ShinsuStats implements INBTSerializable<CompoundNBT> {

    public static final int ENTITY_RANGE = 32;
    private static final String LEVEL_UP = "entity." + TowerOfGod.MOD_ID + ".level_up";
    private final Map<ShinsuTechnique, Integer> known;
    private final Map<ShinsuTechnique, Integer> cooldowns;
    private final Map<ShinsuTechnique, Integer> experience;
    private final List<ShinsuTechniqueInstance> techniques;
    private int level;
    private int shinsu;
    private int baangs;
    private double resistance;
    private double tension;
    private ShinsuQuality quality;
    private ShinsuShape shape;

    public ShinsuStats() {
        this(1, 0, 0, 1, 1, ShinsuQuality.NONE, ShinsuShape.NONE);
    }

    private ShinsuStats(int level, int shinsu, int baangs, double resistance, double tension, ShinsuQuality quality, ShinsuShape shape) {
        this.level = level;
        this.shinsu = shinsu;
        this.baangs = baangs;
        this.resistance = resistance;
        this.tension = tension;
        this.quality = quality;
        this.shape = shape;
        known = new EnumMap<>(ShinsuTechnique.class);
        experience = new EnumMap<>(ShinsuTechnique.class);
        cooldowns = new EnumMap<>(ShinsuTechnique.class);
        techniques = new ArrayList<>();
    }

    @Nonnull
    public static ShinsuStats get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(ShinsuStats::new);
    }

    public static double getNetResistance(ServerWorld world, Entity user, Entity target) {
        ShinsuStats targetStats = get(target);
        ShinsuStats userStats = get(user);
        return targetStats.getResistance(world) / userStats.getTension(world);
    }

    public List<ShinsuTechniqueInstance> getTechniques() {
        return techniques;
    }

    public void onKill(Entity owner, ShinsuStats killed) {
        addMaxShinsu(getGainedShinsu(killed.getMaxShinsu()));
        addMaxBaangs(getGainedBaangs(killed.getMaxBaangs()));
        multiplyBaseResistance(getGainedResistance(killed.getRawResistance()));
        multiplyBaseTension(getGainedTension(killed.getRawTension()));
        for (ShinsuTechnique technique : ShinsuTechnique.values()) {
            int initial = getTechniqueLevel(technique);
            addExperience(technique, killed.getTechniqueLevel(technique));
            int after = getTechniqueLevel(technique);
            if (initial != after) {
                owner.sendMessage(new TranslationTextComponent(LEVEL_UP, technique.getText(), after), Util.DUMMY_UUID);
            }
        }
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new UpdateClientKnownPacket(known));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new UpdateStatsMetersPacket(getShinsu(), getMaxShinsu(), getBaangs(), getMaxBaangs()));
        }
    }

    protected int getTechniqueLevelCap() {
        return getLevel() * 2;
    }

    protected int getGainedShinsu(int killed) {
        int cap = getLevel() * 15 + 15;
        int current = getMaxShinsu();
        if (current < cap) {
            int change = Math.min(cap - current, killed / 7);
            return current <= 0 ? Math.max(1, change) : change;
        }
        return 0;
    }

    protected int getGainedBaangs(int killed) {
        int current = getMaxBaangs();
        int cap = 2 + getLevel() / 2;
        if (current < cap) {
            int change = Math.min(cap - current, killed / 3);
            return current <= 0 ? Math.max(1, change) : change;
        }
        return 0;
    }

    protected double getGainedTension(double killed) {
        double cap = 1 + getLevel() / 4.0;
        double current = getRawTension();
        return current < cap ? Math.min(cap / current, 1 + killed / 20) : 1;
    }

    protected double getGainedResistance(double killed) {
        double cap = 1 + getLevel() / 4.0;
        double current = getRawResistance();
        return current < cap ? Math.min(cap / current, 1 + killed / 20) : 1;
    }

    public void addExperience(ShinsuTechnique technique, int amount) {
        int initial = getExperience(technique);
        int level = getTechniqueLevel(technique);
        int net = 0;
        int cap = getTechniqueLevelCap();
        while (amount > 0) {
            if (initial + net >= cap) {
                initial = Math.max(initial - amount, 0);
                break;
            } else if (amount >= initial) {
                net++;
                initial = getLevelUpExperience(level + net);
                amount -= initial;
            } else {
                initial -= amount;
            }
        }
        addKnownTechnique(technique, net);
        experience.put(technique, initial);
    }

    public int getExperience(ShinsuTechnique technique) {
        return experience.getOrDefault(technique, getLevelUpExperience(getTechniqueLevel(technique)));
    }

    private int getLevelUpExperience(int level) {
        return (int) Math.pow(2, level) * 2;
    }

    public void addTechnique(ShinsuTechniqueInstance technique) {
        techniques.add(technique);
    }

    public void removeTechnique(ShinsuTechniqueInstance technique) {
        techniques.remove(technique);
    }

    public int getTechniqueLevel(ShinsuTechnique technique) {
        return known.getOrDefault(technique, 0);
    }

    public void addKnownTechnique(ShinsuTechnique technique, int amt) {
        known.put(technique, Math.max(0, getTechniqueLevel(technique) + amt));
    }

    public int getLevel() {
        return level;
    }

    public void addLevel(int amount) {
        level = Math.max(1, level + amount);
    }

    public int getShinsu() {
        int shinsu = getMaxShinsu();
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            shinsu -= technique.getShinsuUse();
        }
        return shinsu;
    }

    public int getMaxShinsu() {
        return shinsu;
    }

    public void addMaxShinsu(int amount) {
        shinsu = Math.max(0, shinsu + amount);
    }

    public int getBaangs() {
        int baangs = getMaxBaangs();
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            baangs -= technique.getBaangsUse();
        }
        return baangs;
    }

    public int getMaxBaangs() {
        return baangs;
    }

    public void addMaxBaangs(int amount) {
        baangs = Math.max(0, baangs + amount);
    }

    public double getRawResistance() {
        return resistance;
    }

    public double getRawTension() {
        return tension;
    }

    public double getResistance(ServerWorld world) {
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(world);
        if (property != null) {
            return resistance * property.getShinsuDensity();
        }
        return resistance;
    }

    public void multiplyBaseResistance(double factor) {
        resistance *= factor;
    }

    public double getTension(ServerWorld world) {
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(world);
        if (property != null) {
            return tension * property.getShinsuDensity();
        }
        return tension;
    }

    public void multiplyBaseTension(double factor) {
        tension *= factor;
    }

    public ShinsuQuality getQuality() {
        return quality;
    }

    public void setQuality(ShinsuQuality quality) {
        this.quality = quality;
    }

    public ShinsuShape getShape() {
        return shape;
    }

    public void setShape(ShinsuShape shape) {
        this.shape = shape;
    }

    public int getCooldown(ShinsuTechnique technique) {
        return cooldowns.getOrDefault(technique, 0);
    }

    public void addCooldown(ShinsuTechnique technique, int time) {
        cooldowns.put(technique, time);
    }

    public void tick(ServerWorld world) {
        List<ShinsuTechniqueInstance> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance technique : techniques) {
            technique.tick(world);
            if (technique.ticksLeft() <= 0) {
                technique.remove(world);
            }
        }
        for (ShinsuTechnique technique : known.keySet()) {
            int time = getCooldown(technique);
            if (time > 0) {
                addCooldown(technique, time - 1);
            }
        }
    }

    public void cast(LivingEntity user, ShinsuTechnique technique, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
        cast(user, technique, getTechniqueLevel(technique), target, dir, settings);
    }

    public void cast(LivingEntity user, ShinsuTechnique technique, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
        if (user.world instanceof ServerWorld) {
            Optional<ShinsuTechniqueInstance> used = getTechniques().stream().filter(instance -> instance.getTechnique() == technique && instance.getSettings().equals(settings)).findAny();
            if (technique.getRepeatEffect() == ShinsuTechnique.Repeat.TOGGLE && used.isPresent()) {
                used.get().remove((ServerWorld) user.world);
            } else if (getCooldown(technique) <= 0) {
                ShinsuTechniqueInstance tech = technique.getBuilder().doBuild(user, level, target, dir, settings);
                if (tech != null) {
                    cast((ServerWorld) user.world, tech);
                }
            }
        }
    }

    public void cast(ServerWorld world, ShinsuTechniqueInstance instance) {
        ShinsuTechnique technique = instance.getTechnique();
        for (ShinsuTechniqueInstance inst : new ArrayList<>(getTechniques())) {
            if (instance.isConflicting(inst)) {
                inst.remove(world);
            }
        }
        int cooldown = instance.getCooldown();
        Entity user = instance.getUser(world);
        if (user instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new UpdateInitialCooldownsPacket(technique, cooldown));
        }
        addCooldown(technique, cooldown);
        addTechnique(instance);
        instance.onUse(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("Level", level);
        tag.putInt("Shinsu", shinsu);
        tag.putInt("Baangs", baangs);
        tag.putDouble("Resistance", resistance);
        tag.putDouble("Tension", tension);
        CompoundNBT knownTech = new CompoundNBT();
        for (Map.Entry<ShinsuTechnique, Integer> entry : known.entrySet()) {
            knownTech.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.put("Known", knownTech);
        ListNBT instances = new ListNBT();
        for (ShinsuTechniqueInstance instance : techniques) {
            instances.add(instance.serializeNBT());
        }
        CompoundNBT experience = new CompoundNBT();
        for (Map.Entry<ShinsuTechnique, Integer> entry : this.experience.entrySet()) {
            experience.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.put("Experience", experience);
        tag.put("Techniques", instances);
        tag.putString("Quality", quality.name());
        tag.putString("Shape", shape.name());
        CompoundNBT cool = new CompoundNBT();
        for (ShinsuTechnique tech : cooldowns.keySet()) {
            cool.putInt(tech.name(), cooldowns.get(tech));
        }
        tag.put("Cooldowns", cool);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
        if (nbt.contains("Shinsu", Constants.NBT.TAG_INT)) {
            shinsu = nbt.getInt("Shinsu");
        }
        if (nbt.contains("Baangs", Constants.NBT.TAG_INT)) {
            baangs = nbt.getInt("Baangs");
        }
        if (nbt.contains("Resistance", Constants.NBT.TAG_DOUBLE)) {
            resistance = nbt.getDouble("Resistance");
        }
        if (nbt.contains("Tension", Constants.NBT.TAG_DOUBLE)) {
            tension = nbt.getDouble("Tension");
        }
        if (nbt.contains("Known", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT knownTech = nbt.getCompound("Known");
            for (ShinsuTechnique tech : ShinsuTechnique.values()) {
                String name = tech.name();
                if (knownTech.contains(name, Constants.NBT.TAG_INT)) {
                    known.put(tech, knownTech.getInt(tech.name()));
                }
            }
        }
        if (nbt.contains("Techniques", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("Techniques", Constants.NBT.TAG_COMPOUND);
            for (INBT data : list) {
                ShinsuTechnique type = ShinsuTechnique.valueOf(((CompoundNBT) data).getString("Technique"));
                ShinsuTechniqueInstance tech = type.getBuilder().emptyBuild();
                tech.deserializeNBT((CompoundNBT) data);
                techniques.add(tech);
            }
        }
        if (nbt.contains("Experience", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT experience = nbt.getCompound("Experience");
            for (ShinsuTechnique tech : ShinsuTechnique.values()) {
                String name = tech.name();
                if (experience.contains(name, Constants.NBT.TAG_INT)) {
                    this.experience.put(tech, experience.getInt(tech.name()));
                }
            }
        }
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            quality = ShinsuQuality.valueOf(nbt.getString("Quality"));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            shape = ShinsuShape.valueOf(nbt.getString("Shape"));
        }
        if (nbt.contains("Cooldowns", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT cool = nbt.getCompound("Cooldowns");
            for (String key : cool.keySet()) {
                if (cool.contains(key, Constants.NBT.TAG_INT)) {
                    cooldowns.put(ShinsuTechnique.valueOf(key), cool.getInt(key));
                }
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(ShinsuStats.class)
        public static Capability<ShinsuStats> capability = null;
        private final LazyOptional<ShinsuStats> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

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

    public static class Storage implements Capability.IStorage<ShinsuStats> {

        @Override
        public INBT writeNBT(Capability<ShinsuStats> capability, ShinsuStats instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ShinsuStats> capability, ShinsuStats instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}

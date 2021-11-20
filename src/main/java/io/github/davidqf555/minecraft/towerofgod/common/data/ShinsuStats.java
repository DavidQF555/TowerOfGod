package io.github.davidqf555.minecraft.towerofgod.common.data;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientShinsuDataPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateInitialCooldownsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.*;
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
    private final Map<ShinsuTechniqueType, ShinsuTechniqueData> data;
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
        data = new EnumMap<>(ShinsuTechniqueType.class);
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

    public void addTechnique(ShinsuTechniqueInstance technique) {
        techniques.add(technique);
    }

    public void removeTechnique(ShinsuTechniqueInstance technique) {
        techniques.remove(technique);
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

    public void onKill(Entity owner, ShinsuStats killed) {
        addMaxShinsu(getGainedShinsu(killed.getMaxShinsu()));
        addMaxBaangs(getGainedBaangs(killed.getMaxBaangs()));
        multiplyBaseResistance(getGainedResistance(killed.getRawResistance()));
        multiplyBaseTension(getGainedTension(killed.getRawTension()));
        for (ShinsuTechnique technique : ShinsuTechnique.values()) {
            ShinsuTechniqueType type = technique.getType();
            ShinsuTechniqueData data = getData(type);
            int initial = data.getLevel();
            addExperience(type, killed.getData(type).getLevel());
            int after = data.getLevel();
            if (initial != after) {
                owner.sendMessage(new TranslationTextComponent(LEVEL_UP, technique.getText(), after), Util.DUMMY_UUID);
            }
        }
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new UpdateClientShinsuDataPacket(data));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new UpdateShinsuMeterPacket(getShinsu(), getMaxShinsu()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new UpdateBaangsMeterPacket(getBaangs(), getMaxBaangs()));
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

    public void addExperience(ShinsuTechniqueType type, int amount) {
        ShinsuTechniqueData data = getData(type);
        int exp = getExperience(type);
        int level = data.getLevel();
        int cap = getTechniqueLevelCap();
        while (amount > 0) {
            if (level >= cap) {
                exp = Math.max(exp - amount, 0);
                break;
            } else if (amount >= exp) {
                level++;
                exp = getLevelUpExperience(level);
                amount -= exp;
            } else {
                exp -= amount;
                break;
            }
        }
        data.setLevel(level);
        data.setExperience(exp);
    }

    private int getLevelUpExperience(int level) {
        return (int) Math.pow(2, level) * 2;
    }

    public int getExperience(ShinsuTechniqueType type) {
        return getData(type).getExperience();
    }

    public ShinsuTechniqueData getData(ShinsuTechniqueType type) {
        return data.computeIfAbsent(type, p -> new ShinsuTechniqueData());
    }

    public void tick(ServerWorld world) {
        List<ShinsuTechniqueInstance> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance technique : techniques) {
            technique.tick(world);
            if (technique.ticksLeft() <= 0) {
                technique.remove(world);
            }
        }
        for (ShinsuTechniqueType type : ShinsuTechniqueType.values()) {
            ShinsuTechniqueData data = getData(type);
            int cooldown = data.getCooldown();
            if (cooldown > 0) {
                data.setCooldown(cooldown - 1);
            }
        }
    }

    public void cast(LivingEntity user, ShinsuTechnique technique, @Nullable Entity target, Vector3d dir) {
        cast(user, technique, getData(technique.getType()).getLevel(), target, dir);
    }

    public void cast(LivingEntity user, ShinsuTechnique technique, int level, @Nullable Entity target, Vector3d dir) {
        if (user.world instanceof ServerWorld) {
            Optional<ShinsuTechniqueInstance> used = getTechniques().stream().filter(instance -> instance.getTechnique() == technique).findAny();
            if (technique.getRepeatEffect() == ShinsuTechnique.Repeat.TOGGLE && used.isPresent()) {
                used.get().remove((ServerWorld) user.world);
            } else if (getData(technique.getType()).getCooldown() <= 0) {
                ShinsuTechniqueInstance tech = technique.getBuilder().doBuild(user, level, target, dir);
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
        getData(technique.getType()).setCooldown(cooldown);
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
        tag.putString("Quality", quality.name());
        tag.putString("Shape", shape.name());
        ListNBT instances = new ListNBT();
        for (ShinsuTechniqueInstance instance : techniques) {
            instances.add(instance.serializeNBT());
        }
        tag.put("Techniques", instances);
        CompoundNBT data = new CompoundNBT();
        for (Map.Entry<ShinsuTechniqueType, ShinsuTechniqueData> entry : this.data.entrySet()) {
            data.put(entry.getKey().name(), entry.getValue().serializeNBT());
        }
        tag.put("Data", data);
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
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            quality = ShinsuQuality.valueOf(nbt.getString("Quality"));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            shape = ShinsuShape.valueOf(nbt.getString("Shape"));
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
        if (nbt.contains("Data", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = nbt.getCompound("Data");
            for (String key : data.keySet()) {
                ShinsuTechniqueData d = new ShinsuTechniqueData();
                d.deserializeNBT(data.getCompound(key));
                this.data.put(ShinsuTechniqueType.valueOf(key), d);
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
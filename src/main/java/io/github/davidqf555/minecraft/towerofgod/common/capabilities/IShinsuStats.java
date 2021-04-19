package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import com.google.common.collect.Maps;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IShinsuStats extends INBTSerializable<CompoundNBT> {

    @Nonnull
    static IShinsuStats get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(ShinsuStats::new);
    }

    static double getTotalResistance(ServerWorld world, Entity user, Entity target) {
        IShinsuStats targetStats = get(target);
        IShinsuStats userStats = get(user);
        return targetStats.getResistance(world) / userStats.getTension(world);
    }

    List<ShinsuTechniqueInstance> getTechniques();

    void addTechnique(ShinsuTechniqueInstance technique);

    void removeTechnique(ShinsuTechniqueInstance technique);

    int getTechniqueLevel(ShinsuTechnique technique);

    void addKnownTechnique(ShinsuTechnique technique, int amt);

    int getLevel();

    void addLevel(int amount);

    int getShinsu();

    int getMaxShinsu();

    void addMaxShinsu(int amount);

    int getBaangs();

    int getMaxBaangs();

    void addMaxBaangs(int amount);

    double getResistance(ServerWorld world);

    void multiplyBaseResistance(double factor);

    double getTension(ServerWorld world);

    void multiplyBaseTension(double factor);

    default ShinsuQuality getQuality() {
        return ShinsuQuality.NONE;
    }

    void setQuality(ShinsuQuality quality);

    default ShinsuShape getShape() {
        return ShinsuShape.NONE;
    }

    void setShape(ShinsuShape shape);

    int getCooldown(ShinsuTechnique technique);

    void addCooldown(ShinsuTechnique technique, int time);

    default void cast(LivingEntity user, ShinsuTechnique technique, @Nullable Entity target, Vector3d dir) {
        cast(user, technique, getTechniqueLevel(technique), target, dir);
    }

    default void cast(LivingEntity user, ShinsuTechnique technique, int level, @Nullable Entity target, Vector3d dir) {
        if (getCooldown(technique) <= 0 && user.world instanceof ServerWorld) {
            ShinsuTechnique.Builder<? extends ShinsuTechniqueInstance> builder = technique.getBuilder();
            if (builder.canCast(user, level, target, dir)) {
                ShinsuTechniqueInstance tech = technique.getBuilder().build(user, level, target, dir);
                if (tech != null) {
                    addCooldown(technique, tech.getCooldown());
                    addTechnique(tech);
                    tech.onUse((ServerWorld) user.world);
                }
            }
        }
    }

    default void tick(ServerWorld world) {
    }

    class ShinsuStats implements IShinsuStats {

        private final Map<ShinsuTechnique, Integer> known;
        private final Map<ShinsuTechnique, Integer> cooldowns;
        private final List<ShinsuTechniqueInstance> techniques;
        private int level;
        private int shinsu;
        private int baangs;
        private double resistance;
        private double tension;
        private ShinsuQuality quality;
        private ShinsuShape shape;

        public ShinsuStats() {
            this(1, 0, 0, 1, 1, ShinsuQuality.NONE, ShinsuShape.NONE, Maps.newEnumMap(ShinsuTechnique.class), Maps.newEnumMap(ShinsuTechnique.class), new ArrayList<>());
        }

        private ShinsuStats(int level, int shinsu, int baangs, double resistance, double tension, ShinsuQuality quality, ShinsuShape shape, Map<ShinsuTechnique, Integer> known, Map<ShinsuTechnique, Integer> cooldowns, List<ShinsuTechniqueInstance> techniques) {
            this.level = level;
            this.shinsu = shinsu;
            this.baangs = baangs;
            this.resistance = resistance;
            this.tension = tension;
            this.quality = quality;
            this.shape = shape;
            this.known = known;
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
        public int getTechniqueLevel(ShinsuTechnique technique) {
            return known.getOrDefault(technique, 0);
        }

        @Override
        public void addKnownTechnique(ShinsuTechnique technique, int amt) {
            known.put(technique, getTechniqueLevel(technique) + amt);
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public void addLevel(int amount) {
            level = Math.max(1, level + amount);
        }

        @Override
        public int getShinsu() {
            int shinsu = this.shinsu;
            for (ShinsuTechniqueInstance technique : getTechniques()) {
                shinsu -= technique.getTechnique().getShinsuUse();
            }
            return shinsu;
        }

        @Override
        public int getMaxShinsu() {
            return shinsu;
        }

        @Override
        public void addMaxShinsu(int amount) {
            shinsu = Math.max(0, shinsu + amount);
        }

        @Override
        public int getBaangs() {
            int baangs = this.baangs;
            for (ShinsuTechniqueInstance technique : getTechniques()) {
                baangs -= technique.getTechnique().getBaangUse();
            }
            return baangs;
        }

        @Override
        public int getMaxBaangs() {
            return baangs;
        }

        @Override
        public void addMaxBaangs(int amount) {
            baangs = Math.max(0, baangs + amount);
        }

        @Override
        public double getResistance(ServerWorld world) {
            FloorProperty property = FloorDimensionsHelper.getFloorProperty(world);
            if (property != null) {
                return resistance * property.getShinsuDensity();
            }
            return resistance;
        }

        @Override
        public void multiplyBaseResistance(double factor) {
            resistance *= factor;
        }

        @Override
        public double getTension(ServerWorld world) {
            FloorProperty property = FloorDimensionsHelper.getFloorProperty(world);
            if (property != null) {
                return tension * property.getShinsuDensity();
            }
            return tension;
        }

        @Override
        public void multiplyBaseTension(double factor) {
            tension *= factor;
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
        public ShinsuShape getShape() {
            return shape;
        }

        @Override
        public void setShape(ShinsuShape shape) {
            this.shape = shape;
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

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("Level", level);
            tag.putInt("Shinsu", shinsu);
            tag.putInt("Baangs", baangs);
            tag.putDouble("Resistance", resistance);
            tag.putDouble("Tension", tension);
            CompoundNBT knownTech = new CompoundNBT();
            for (ShinsuTechnique tech : known.keySet()) {
                knownTech.putInt(tech.name(), known.get(tech));
            }
            tag.put("Known", knownTech);
            ListNBT instances = new ListNBT();
            for (ShinsuTechniqueInstance instance : techniques) {
                instances.add(instance.serializeNBT());
            }
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
            level = nbt.getInt("Level");
            shinsu = nbt.getInt("Shinsu");
            baangs = nbt.getInt("Baangs");
            resistance = nbt.getDouble("Resistance");
            tension = nbt.getDouble("Tension");
            CompoundNBT knownTech = nbt.getCompound("Known");
            for (ShinsuTechnique tech : ShinsuTechnique.values()) {
                known.put(tech, knownTech.getInt(tech.name()));
            }
            ListNBT list = nbt.getList("Techniques", Constants.NBT.TAG_COMPOUND);
            for (INBT data : list) {
                ShinsuTechnique type = ShinsuTechnique.valueOf(((CompoundNBT) data).getString("Technique"));
                ShinsuTechniqueInstance tech = type.getBuilder().emptyBuild();
                tech.deserializeNBT((CompoundNBT) data);
                techniques.add(tech);
            }
            quality = ShinsuQuality.valueOf(nbt.getString("Quality"));
            shape = ShinsuShape.valueOf(nbt.getString("Shape"));
            CompoundNBT cool = nbt.getCompound("Cooldowns");
            for (String key : cool.keySet()) {
                cooldowns.put(ShinsuTechnique.valueOf(key), cool.getInt(key));
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
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IShinsuStats> capability, IShinsuStats instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}

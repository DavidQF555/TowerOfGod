package com.davidqf.minecraft.towerofgod.entities.shinsu;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechniques;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

public abstract class ShinsuUser extends CreatureEntity {

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".shinsuuser";
    private ShinsuQuality quality;
    private final List<ShinsuTechniques> preferred;
    private final Map<ShinsuTechniques, Integer> knownTechniques;
    private final List<ShinsuTechnique> techniques;
    private final Map<ShinsuTechniques, Integer> cooldown;

    @Nonnull
    public static IStats getStats(Entity user) {
        return user.getCapability(StatsProvider.capability).orElse(new Stats(0, 0, 0, 1, 0));
    }

    public ShinsuUser(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        setQuality(ShinsuQuality.NONE);
        knownTechniques = new HashMap<>();
        techniques = new ArrayList<>();
        preferred = new ArrayList<>();
        cooldown = new HashMap<>();
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new UseShinsuTechniqueGoal());
    }

    public List<ShinsuTechniques> getPreferred() {
        return preferred;
    }

    @Nonnull
    public ShinsuQuality getQuality() {
        return quality;
    }

    public void setQuality(@Nullable ShinsuQuality quality) {
        this.quality = quality;
    }

    public Map<ShinsuTechniques, Integer> getKnownTechniques() {
        return knownTechniques;
    }

    @Override
    public void livingTick() {
        for (int i = techniques.size() - 1; i >= 0; i--) {
            ShinsuTechnique attack = techniques.get(i);
            attack.tick(world);
            if (attack.ticksLeft() <= 0) {
                attack.onEnd(world);
                techniques.remove(i);
            }
        }

        for (ShinsuTechniques tech : new ArrayList<>(cooldown.keySet())) {
            cooldown.put(tech, cooldown.get(tech) - 1);
            if (cooldown.get(tech) <= 0) {
                cooldown.remove(tech);
            }
        }
        super.livingTick();
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT user = (CompoundNBT) nbt.get(TAG_KEY);
            int pref = user.getInt("Preferred");
            for (int i = 1; i <= pref; i++) {
                preferred.add(ShinsuTechniques.get(user.getString("Preferred" + i)));
            }
            setQuality(ShinsuQuality.get(user.getString("Quality")));
            int known = user.getInt("KnownTechniques");
            for (int i = 1; i <= known; i++) {
                String[] s = user.getString("KnownTechniques" + i).split(" ");
                knownTechniques.put(ShinsuTechniques.get(s[0]), Integer.parseInt(s[1]));
            }
            int tech = user.getInt("Techniques");
            for (int i = 1; i <= tech; i++) {
                ShinsuTechniques t = ShinsuTechniques.get(user.getString("TechniqueType" + i));
                CompoundNBT com = (CompoundNBT) user.get("Techniques" + i);
                ShinsuTechnique technique = t.newEmptyInstance();
                technique.deserializeNBT(com);
                techniques.add(technique);
            }
            int cooldowns = user.getInt("Cooldowns");
            for (int i = 1; i <= cooldowns; i++) {
                String[] s = user.getString("Cooldowns" + i).split(" ");
                cooldown.put(ShinsuTechniques.get(s[0]), Integer.parseInt(s[1]));
            }
        }
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT nbt) {
        super.writeAdditional(nbt);
        CompoundNBT user = new CompoundNBT();
        user.putInt("Preferred", preferred.size());
        for (int i = 1; i <= preferred.size(); i++) {
            user.putString("Preferred" + i, preferred.get(i - 1).name());
        }
        user.putString("Quality", getQuality().name());
        user.putInt("KnownTechniques", knownTechniques.size());
        int count = 1;
        for (ShinsuTechniques tech : knownTechniques.keySet()) {
            user.putString("KnownTechniques" + count, tech.name() + " " + knownTechniques.get(tech));
            count++;
        }
        user.putInt("Techniques", techniques.size());
        for (int i = 1; i <= techniques.size(); i++) {
            ShinsuTechnique tech = techniques.get(i - 1);
            user.putString("TechniqueType" + i, tech.getTechnique().name());
            user.put("Techniques" + i, tech.serializeNBT());
        }
        user.putInt("Cooldowns", cooldown.size());
        int c = 1;
        for (ShinsuTechniques tech : cooldown.keySet()) {
            user.putString("Cooldowns" + c, tech.name() + " " + knownTechniques.get(tech));
            c++;
        }
        nbt.put(TAG_KEY, user);
    }

    public class UseShinsuTechniqueGoal extends Goal {

        private final List<ShinsuTechnique> possible;

        public UseShinsuTechniqueGoal() {
            possible = new ArrayList<>();
        }

        @Override
        public boolean shouldExecute() {
            for (ShinsuTechniques tech : knownTechniques.keySet()) {
                int level = knownTechniques.get(tech);
                if (level > 0) {
                    ShinsuTechnique technique;
                    try {
                        technique = tech.getTechniqueClass().getConstructor(LivingEntity.class, int.class).newInstance(ShinsuUser.this, level);
                    } catch (NoSuchMethodException noSuchMethodException) {
                        LivingEntity target = getAttackTarget();
                        if (target != null) {
                            try {
                                technique = tech.getTechniqueClass().getConstructor(LivingEntity.class, int.class, LivingEntity.class).newInstance(ShinsuUser.this, level, target);
                            } catch (NoSuchMethodException exception) {
                                try {
                                    technique = tech.getTechniqueClass().getConstructor(LivingEntity.class, int.class, Vector3d.class).newInstance(ShinsuUser.this, level, target.getPositionVec().add(0, target.getEyeHeight(), 0));
                                } catch (Exception e) {
                                    continue;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    if (technique.canUse(world) && !cooldown.containsKey(tech) && technique.isIdeal(world) && (!techniques.contains(technique) || tech.canStack())) {
                        possible.add(technique);
                    }
                }
            }
            return !possible.isEmpty();
        }

        @Override
        public void resetTask() {
            possible.clear();
        }

        @Override
        public void startExecuting() {
            List<ShinsuTechnique> best = new ArrayList<>();
            for (ShinsuTechniques pref : getPreferred()) {
                for (ShinsuTechnique pos : possible) {
                    if (pref.equals(ShinsuTechniques.get(pos))) {
                        best.add(pos);
                        break;
                    }
                }
            }
            ShinsuTechnique technique;
            if (best.isEmpty()) {
                technique = possible.get((int) (possible.size() * Math.random()));
            } else {
                technique = best.get((int) (best.size() * Math.random()));
            }
            ShinsuTechniques tech = ShinsuTechniques.get(technique);
            if (tech != null) {
                techniques.add(technique);
                cooldown.put(tech, tech.getCooldown());
                technique.onUse(world);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return false;
        }
    }

    public interface IStats {

        int getLevel();

        void setLevel(int val);

        int getShinsu();

        void setShinsu(int amt);

        int getBaangs();

        void setBaangs(int amt);

        double getResistance();

        void setResistance(double val);

        double getTension();

        void setTension(double amt);

    }

    public static class Stats implements IStats {

        private int level;
        private int shinsu;
        private int baangs;
        private double resistance;
        private double tension;

        public Stats() {
            level = 1;
            shinsu = 50;
            baangs = 1;
            resistance = 0;
            tension = 1;
        }

        public Stats(int level, int shinsu, int baangs, double resistance, double tension) {
            this.level = level;
            this.shinsu = shinsu;
            this.baangs = baangs;
            this.resistance = resistance;
            this.tension = tension;
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public void setLevel(int val) {
            level = val;
        }

        @Override
        public int getShinsu() {
            return shinsu;
        }

        @Override
        public void setShinsu(int amt) {
            shinsu = amt;
        }

        @Override
        public int getBaangs() {
            return baangs;
        }

        @Override
        public void setBaangs(int amt) {
            baangs = amt;
        }

        @Override
        public double getResistance() {
            return resistance;
        }

        @Override
        public void setResistance(double val) {
            resistance = val;
        }

        @Override
        public double getTension() {
            return tension;
        }

        @Override
        public void setTension(double amt) {
            tension = amt;
        }

        public static class Factory implements Callable<IStats> {
            @Override
            public IStats call() {
                return new Stats();
            }
        }
    }

    public static class StatsProvider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(IStats.class)
        public static Capability<IStats> capability = null;
        private final LazyOptional<IStats> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

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

    public static class StatsStorage implements Capability.IStorage<IStats> {

        @Override
        public INBT writeNBT(Capability<IStats> capability, IStats instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("level", instance.getLevel());
            tag.putInt("shinsu", instance.getShinsu());
            tag.putInt("baangs", instance.getBaangs());
            tag.putDouble("resistance", instance.getResistance());
            tag.putDouble("tension", instance.getTension());
            return tag;
        }

        @Override
        public void readNBT(Capability<IStats> capability, IStats instance, Direction side, INBT nbt) {
            CompoundNBT tag = (CompoundNBT) nbt;
            instance.setLevel(tag.getInt("level"));
            instance.setShinsu(tag.getInt("shinsu"));
            instance.setBaangs(tag.getInt("baangs"));
            instance.setResistance(tag.getDouble("resistance"));
            instance.setTension(tag.getDouble("tension"));
        }
    }
}

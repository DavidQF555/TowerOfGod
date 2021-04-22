package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public interface IShinsuUser<T extends LivingEntity> {

    default int getInitialMaxShinsu() {
        return 10 + (int) (getShinsuLevel() * getGroup().getShinsu() * (getShinsuUserEntity().getRNG().nextGaussian() * 0.25 + 1) + 0.5);
    }

    default int getInitialMaxBaangs() {
        return 1 + (int) (0.05 * getShinsuLevel() * getGroup().getBaangs() * (getShinsuUserEntity().getRNG().nextGaussian() * 0.25 + 1) + 0.5);
    }

    default double getInitialResistance() {
        return 1 + getShinsuLevel() * 0.025 * getGroup().getResistance() * (getShinsuUserEntity().getRNG().nextGaussian() * 0.25 + 1);
    }

    default double getInitialTension() {
        return 1 + getShinsuLevel() * 0.025 * getGroup().getTension() * (getShinsuUserEntity().getRNG().nextGaussian() * 0.25 + 1);
    }

    T getShinsuUserEntity();

    default void initializeShinsuStats(IServerWorld world) {
        T entity = getShinsuUserEntity();
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(world.getWorld());
        int floor = property == null ? 1 : property.getLevel();
        setShinsuLevel(getInitialShinsuLevel(floor));
        setGroup(getInitialGroup());
        IShinsuStats stats = IShinsuStats.get(entity);
        stats.addLevel(floor - stats.getLevel());
        stats.addMaxShinsu(getInitialMaxShinsu());
        stats.addMaxBaangs(getInitialMaxBaangs());
        stats.multiplyBaseResistance(getInitialResistance());
        stats.multiplyBaseTension(getInitialTension());
        for (ShinsuTechnique technique : getInitialShinsuTechniques()) {
            stats.addKnownTechnique(technique, 1);
        }
        stats.setQuality(getInitialQuality());
        stats.setShape(getInitialShape());
    }

    default double getPreferredTechniqueChance() {
        return 0.75;
    }

    default ShinsuTechnique[] getInitialShinsuTechniques() {
        List<ShinsuTechnique> all = ShinsuTechnique.getObtainableTechniques();
        ShinsuTechnique[] preferred = getPreferredTechniques();
        Random rand = getShinsuUserEntity().getRNG();
        ShinsuTechnique[] techniques = new ShinsuTechnique[getInitialTechniquesTotalLevel()];
        for (int i = 0; i < techniques.length; i++) {
            if (preferred.length > 0 && rand.nextDouble() < getPreferredTechniqueChance()) {
                techniques[i] = preferred[rand.nextInt(preferred.length)];
            } else {
                techniques[i] = all.get(rand.nextInt(all.size()));
            }
        }
        return techniques;
    }

    default int getInitialTechniquesTotalLevel() {
        return getShinsuLevel() / 4 + getShinsuUserEntity().getRNG().nextInt(4);
    }

    default double getPreferredQualityChance() {
        return 0.75;
    }

    default ShinsuQuality getInitialQuality() {
        ShinsuQuality[] pref = getPreferredQualities();
        Random rand = getShinsuUserEntity().getRNG();
        if (pref.length > 0 && rand.nextDouble() < getPreferredQualityChance()) {
            return pref[rand.nextInt(pref.length)];
        } else {
            ShinsuQuality[] qualities = ShinsuQuality.values();
            return qualities[rand.nextInt(qualities.length)];
        }
    }

    default double getPreferredShapeChance() {
        return 0.75;
    }

    default ShinsuShape getInitialShape() {
        ShinsuShape[] pref = getPreferredShapes();
        Random rand = getShinsuUserEntity().getRNG();
        if (pref.length > 0 && rand.nextDouble() < getPreferredShapeChance()) {
            return pref[rand.nextInt(pref.length)];
        } else {
            ShinsuShape[] shapes = ShinsuShape.values();
            return shapes[rand.nextInt(shapes.length)];
        }
    }

    default ShinsuTechnique[] getPreferredTechniques() {
        return getGroup().getPreferredTechniques();
    }

    default ShinsuQuality[] getPreferredQualities() {
        return getGroup().getQualities();
    }

    default ShinsuShape[] getPreferredShapes() {
        return getGroup().getShapes();
    }

    default Group getInitialGroup() {
        List<Group> groups = new ArrayList<>(Arrays.asList(Group.values()));
        groups.remove(Group.NONE);
        return groups.get(getShinsuUserEntity().getRNG().nextInt(groups.size()));
    }

    default int getInitialShinsuLevel(int floor) {
        int min = getMinInitialLevel(floor);
        int total = getMaxInitialLevel(floor) - min;
        double current = 0;
        double random = getShinsuUserEntity().getRNG().nextDouble();
        double rate = 0.8;
        double choose = 1;
        double success = 1;
        double fail = Math.pow(1 - rate, total - 1);
        for (int i = 0; i < total; i++) {
            double chance = choose * success * fail;
            current += chance;
            if (random < current) {
                return i + min;
            }
            choose *= (total - i - 1.0) / (i + 1);
            success *= rate;
            fail /= 1 - rate;
        }
        return total;
    }

    default int getMinInitialLevel(int floor) {
        return 1;
    }

    int getMaxInitialLevel(int floor);

    int getShinsuLevel();

    void setShinsuLevel(int level);

    Group getGroup();

    void setGroup(Group group);

    class CastShinsuGoal<T extends MobEntity & IShinsuUser<T>> extends Goal {

        private final T entity;
        private final IShinsuStats stats;
        private ShinsuTechnique technique;
        private LivingEntity target;

        public CastShinsuGoal(T entity) {
            this.entity = entity;
            stats = IShinsuStats.get(entity);
            technique = null;
            target = null;
        }

        @Override
        public boolean shouldExecute() {
            target = entity.getAttackTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            List<ShinsuTechnique> tech = new ArrayList<>();
            for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                ShinsuTechnique.Builder<? extends ShinsuTechniqueInstance> builder = technique.getBuilder();
                Vector3d dir = entity.canEntityBeSeen(target) ? target.getEyePosition(1).subtract(entity.getEyePosition(1)).normalize() : entity.getLookVec();
                if (stats.getCooldown(technique) <= 0 && builder.canCast(entity, stats.getTechniqueLevel(technique), target, dir)) {
                    tech.add(technique);
                }
            }
            if (tech.isEmpty()) {
                return false;
            }
            technique = tech.get(entity.getRNG().nextInt(tech.size()));
            return true;
        }

        @Override
        public void startExecuting() {
            Vector3d dir = (target != null && entity.canEntityBeSeen(target)) ? target.getEyePosition(1).subtract(entity.getEyePosition(1)).normalize() : entity.getLookVec();
            stats.cast(entity, technique, target, dir);
        }

        @Override
        public void resetTask() {
            technique = null;
            target = null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return false;
        }

    }

}
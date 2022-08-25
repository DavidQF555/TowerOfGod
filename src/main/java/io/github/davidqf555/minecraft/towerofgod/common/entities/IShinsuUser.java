package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorProperty;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface IShinsuUser {

    ShinsuStats getShinsuStats();

    default int getLevel() {
        return getShinsuStats().getLevel();
    }

    default int getInitialMaxShinsu(Random random) {
        Group group = getGroup();
        return 10 + (int) (getLevel() * (group == null ? 1 : group.getShinsu()) * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default int getInitialMaxBaangs(Random random) {
        Group group = getGroup();
        return 1 + (int) (0.05 * getLevel() * (group == null ? 1 : group.getBaangs()) * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default double getInitialResistance(Random random) {
        Group group = getGroup();
        return 1 + getLevel() * 0.025 * (group == null ? 1 : group.getResistance()) * (random.nextGaussian() * 0.25 + 1);
    }

    default double getInitialTension(Random random) {
        Group group = getGroup();
        return 1 + getLevel() * 0.025 * (group == null ? 1 : group.getTension()) * (random.nextGaussian() * 0.25 + 1);
    }

    default void initializeShinsuStats(IServerWorld world) {
        Random random = world.getRandom();
        ShinsuStats stats = getShinsuStats();
        FloorProperty property = FloorDimensionsHelper.getFloorProperty(world.getLevel());
        int floor = property == null ? 1 : property.getLevel();
        stats.addLevel(getInitialShinsuLevel(random, floor) - stats.getLevel());
        setGroup(getInitialGroup(random));
        stats.addMaxShinsu(getInitialMaxShinsu(random) - stats.getMaxShinsu());
        stats.addMaxBaangs(getInitialMaxBaangs(random) - stats.getMaxBaangs());
        stats.multiplyBaseResistance(getInitialResistance(random) / stats.getRawResistance());
        stats.multiplyBaseTension(getInitialTension(random) / stats.getRawTension());
        for (ShinsuTechniqueType type : getInitialTechniqueTypes(random)) {
            ShinsuTypeData data = stats.getData(type);
            data.setLevel(data.getLevel() + 1);
        }
        stats.setQuality(getInitialQuality(random));
        stats.setShape(getInitialShape(random));
    }

    default double getPreferredTechniqueTypeChance() {
        return 0.75;
    }

    default ShinsuTechniqueType[] getInitialTechniqueTypes(Random random) {
        ShinsuTechniqueType[] all = ShinsuTechniqueType.values();
        ShinsuTechniqueType[] preferred = getPreferredTechniqueTypes();
        ShinsuTechniqueType[] types = new ShinsuTechniqueType[getInitialTechniqueTypesTotalLevel(random)];
        for (int i = 0; i < types.length; i++) {
            types[i] = preferred.length > 0 && random.nextDouble() < getPreferredTechniqueTypeChance() ? preferred[random.nextInt(preferred.length)] : all[random.nextInt(all.length)];
        }
        return types;
    }

    default int getInitialTechniqueTypesTotalLevel(Random random) {
        return 1 + (int) (getLevel() * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default void shinsuTick(ServerWorld world) {
        getShinsuStats().tick(world);
    }

    default double getPreferredQualityChance() {
        return 0.75;
    }

    default ShinsuQuality getInitialQuality(Random random) {
        ShinsuQuality[] pref = getPreferredQualities();
        if (pref.length > 0 && random.nextDouble() < getPreferredQualityChance()) {
            return pref[random.nextInt(pref.length)];
        } else {
            List<ShinsuQuality> qualities = new ArrayList<>(ShinsuQualityRegistry.getRegistry().getValues());
            return qualities.get(random.nextInt(qualities.size()));
        }
    }

    default double getPreferredShapeChance() {
        return 0.75;
    }

    @Nullable
    default ShinsuShape getInitialShape(Random random) {
        ShinsuShape[] pref = getPreferredShapes();
        if (pref.length > 0 && random.nextDouble() < getPreferredShapeChance()) {
            return pref[random.nextInt(pref.length)];
        } else {
            ShinsuShape[] shapes = ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]);
            return shapes[random.nextInt(shapes.length)];
        }
    }

    default ShinsuTechniqueType[] getPreferredTechniqueTypes() {
        Group group = getGroup();
        return group == null ? new ShinsuTechniqueType[0] : group.getPreferredTechniqueTypes();
    }

    default ShinsuQuality[] getPreferredQualities() {
        Group group = getGroup();
        return group == null ? new ShinsuQuality[0] : group.getQualities();
    }

    default ShinsuShape[] getPreferredShapes() {
        Group group = getGroup();
        return group == null ? new ShinsuShape[0] : group.getShapes();
    }

    @Nullable
    default Group getInitialGroup(Random random) {
        List<Group> groups = new ArrayList<>(GroupRegistry.getRegistry().getValues());
        return groups.get(random.nextInt(groups.size()));
    }

    default int getInitialShinsuLevel(Random rand, int floor) {
        int min = getMinInitialLevel(floor);
        int total = getMaxInitialLevel(floor) - min;
        double current = 0;
        double random = rand.nextDouble();
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
        return floor;
    }

    int getMaxInitialLevel(int floor);

    @Nullable
    Group getGroup();

    void setGroup(Group group);

    class CastShinsuGoal<T extends MobEntity & IShinsuUser> extends Goal {

        private final T entity;
        private ShinsuTechniqueInstance technique;
        private LivingEntity target;

        public CastShinsuGoal(T entity) {
            this.entity = entity;
            technique = null;
            target = null;
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            List<ShinsuTechniqueInstance> tech = new ArrayList<>();
            for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
                ShinsuTechnique.IFactory<?> builder = technique.getFactory();
                Vector3d dir = entity.canSee(target) ? target.getEyePosition(1).subtract(entity.getEyePosition(1)).normalize() : entity.getLookAngle();
                builder.create(entity, target, dir).ifLeft(tech::add);
            }
            if (tech.isEmpty()) {
                return false;
            }
            technique = tech.get(entity.getRandom().nextInt(tech.size()));
            return true;
        }

        @Override
        public void start() {
            technique.getTechnique().cast(entity, technique);
        }

        @Override
        public void stop() {
            technique = null;
            target = null;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

    }

}

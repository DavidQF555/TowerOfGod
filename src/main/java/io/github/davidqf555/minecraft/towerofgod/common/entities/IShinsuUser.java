package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface IShinsuUser {

    ShinsuStats getShinsuStats();

    default int getShinsuLevel() {
        return getShinsuStats().getLevel();
    }

    default int getInitialMaxShinsu(Random random) {
        Group group = getGroup();
        return 10 + (int) (getShinsuLevel() * (group == null ? 1 : group.getShinsu()) * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default int getInitialMaxBaangs(Random random) {
        Group group = getGroup();
        return 1 + (int) (0.05 * getShinsuLevel() * (group == null ? 1 : group.getBaangs()) * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default double getInitialResistance(Random random) {
        Group group = getGroup();
        return 1 + getShinsuLevel() * 0.025 * (group == null ? 1 : group.getResistance()) * (random.nextGaussian() * 0.25 + 1);
    }

    default double getInitialTension(Random random) {
        Group group = getGroup();
        return 1 + getShinsuLevel() * 0.025 * (group == null ? 1 : group.getTension()) * (random.nextGaussian() * 0.25 + 1);
    }

    default void initializeShinsuStats(ServerLevelAccessor world) {
        Random random = world.getRandom();
        ShinsuStats stats = getShinsuStats();
        stats.addLevel(getInitialShinsuLevel(random) - stats.getLevel());
        setGroup(getInitialGroup(random));
        stats.addMaxShinsu(getInitialMaxShinsu(random) - stats.getMaxShinsu());
        stats.addMaxBaangs(getInitialMaxBaangs(random) - stats.getMaxBaangs());
        stats.multiplyBaseResistance(getInitialResistance(random) / stats.getRawResistance());
        stats.multiplyBaseTension(getInitialTension(random) / stats.getRawTension());
        for (ShinsuTechniqueType type : getInitialTechniqueTypes(random)) {
            ShinsuTypeData data = stats.getData(type);
            data.setLevel(data.getLevel() + 1);
        }
        stats.setAttribute(getInitialAttribute(random));
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
        return 1 + (int) (getShinsuLevel() * (random.nextGaussian() * 0.25 + 1) + 0.5);
    }

    default void shinsuTick(ServerLevel world) {
        getShinsuStats().tick(world);
    }

    default double getPreferredAttributeChance() {
        return 0.75;
    }

    default ShinsuAttribute getInitialAttribute(Random random) {
        ShinsuAttribute[] pref = getPreferredQualities();
        if (pref.length > 0 && random.nextDouble() < getPreferredAttributeChance()) {
            return pref[random.nextInt(pref.length)];
        } else {
            List<ShinsuAttribute> attributes = new ArrayList<>(ShinsuAttributeRegistry.getRegistry().getValues());
            return attributes.get(random.nextInt(attributes.size()));
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

    default ShinsuAttribute[] getPreferredQualities() {
        Group group = getGroup();
        return group == null ? new ShinsuAttribute[0] : group.getAttributes();
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

    default int getInitialShinsuLevel(Random rand) {
        int min = getMinInitialLevel();
        int total = getMaxInitialLevel() - min;
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

    int getMinInitialLevel();

    int getMaxInitialLevel();

    @Nullable
    Group getGroup();

    void setGroup(Group group);

    class CastShinsuGoal<T extends Mob & IShinsuUser> extends Goal {

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
                Vec3 dir = entity.hasLineOfSight(target) ? target.getEyePosition(1).subtract(entity.getEyePosition(1)).normalize() : entity.getLookAngle();
                technique.create(entity, target, dir).ifLeft(tech::add);
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

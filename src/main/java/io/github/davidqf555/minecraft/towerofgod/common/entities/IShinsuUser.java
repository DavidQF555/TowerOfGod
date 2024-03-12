package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.BaangsTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.*;

public interface IShinsuUser<T extends LivingEntity> {

    ShinsuStats getShinsuStats();

    ShinsuQualityData getShinsuQualityData();

    BaangsTechniqueData<T> getShinsuTechniqueData();

    int getShinsuLevel();

    default int getInitialMaxBaangs(Random random) {
        Group group = getGroup();
        double mult = group == null ? 1 : group.getBaangs();
        return Mth.ceil(getShinsuLevel() * mult * (0.8 + random.nextDouble() * 0.4) * 0.25);
    }

    default double getInitialResistance(Random random) {
        Group group = getGroup();
        return Math.pow(Math.pow(10, 1.0 / 99), getShinsuLevel() - 1) * (group == null ? 1 : group.getResistance()) * (0.8 + random.nextDouble() * 0.4);
    }

    default double getInitialTension(Random random) {
        Group group = getGroup();
        return Math.pow(Math.pow(10, 1.0 / 99), getShinsuLevel() - 1) * (group == null ? 1 : group.getTension()) * (0.8 + random.nextDouble() * 0.4);
    }

    default void initializeTechniques(ServerLevelAccessor world, T entity) {
        BaangsTechniqueData<T> data = getShinsuTechniqueData();
        int max = getInitialMaxBaangs(world.getRandom());
        data.setMaxBaangs(max);
        ConfiguredShinsuTechniqueType<?, ?>[] possible = ConfiguredTechniqueTypeRegistry.getRegistry().getValues().stream()
                .filter(type -> Arrays.stream(type.getType().getRequirements()).allMatch(req -> req.isUnlocked(entity)))
                .toArray(ConfiguredShinsuTechniqueType<?, ?>[]::new);
        if (possible.length > 0) {
            Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> levels = new HashMap<>();
            for (int i = 0; i < max; i++) {
                ConfiguredShinsuTechniqueType<?, ?> selected = possible[world.getRandom().nextInt(possible.length)];
                levels.put(selected, levels.getOrDefault(selected, 0) + 1);
            }
            data.setBaangs(levels);
        }
    }

    default void initializeStats(ServerLevelAccessor world) {
        ShinsuStats stats = getShinsuStats();
        Random random = world.getRandom();
        stats.setResistance(getInitialResistance(random));
        stats.setTension(getInitialTension(random));
    }

    default void initializeQuality(ServerLevelAccessor world) {
        ShinsuQualityData stats = getShinsuQualityData();
        stats.setAttribute(getInitialAttribute(world.getRandom()));
        stats.setShape(getInitialShape(world.getRandom()));
    }

    default void initialize(ServerLevelAccessor world, T entity) {
        Random random = world.getRandom();
        setGroup(getInitialGroup(random));
        initializeStats(world);
        initializeQuality(world);
        initializeTechniques(world, entity);
    }

    default void shinsuTick(T user) {
        getShinsuTechniqueData().tick(user);
    }

    default double getPreferredAttributeChance() {
        return 0.75;
    }

    @Nullable
    default ShinsuAttribute getInitialAttribute(Random random) {
        int level = getShinsuLevel();
        ShinsuAttribute[] pref = getPreferredQualities();
        if (pref.length > 0 && random.nextDouble() < getPreferredAttributeChance()) {
            return pref[random.nextInt(pref.length)];
        } else if (level >= 30 && random.nextDouble() < Math.pow(Math.pow(2, 1.0 / 31), level - 29) - 1) {
            ShinsuAttribute[] attributes = ShinsuAttributeRegistry.getRegistry().getValues().toArray(new ShinsuAttribute[0]);
            return attributes[random.nextInt(attributes.length)];
        }
        return null;
    }

    default double getPreferredShapeChance() {
        return 0.75;
    }

    @Nullable
    default ShinsuShape getInitialShape(Random random) {
        int level = getShinsuLevel();
        ShinsuShape[] pref = getPreferredShapes();
        if (pref.length > 0 && random.nextDouble() < getPreferredShapeChance()) {
            return pref[random.nextInt(pref.length)];
        } else if (level >= 30 && random.nextDouble() < Math.pow(Math.pow(2, 1.0 / 31), level - 29) - 1) {
            ShinsuShape[] shapes = ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]);
            return shapes[random.nextInt(shapes.length)];
        }
        return null;
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

    @Nullable
    Group getGroup();

    void setGroup(Group group);

    boolean isCasting();

    void setCasting(boolean casting);

}

package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface IShinsuUser {

    ShinsuStats getShinsuStats();

    ShinsuQualityData getShinsuQualityData();

    ShinsuTechniqueData<?> getShinsuTechniqueData();

    int getLevel();

    default int getInitialMaxShinsu(Random random) {
        Group group = getGroup();
        return MathHelper.ceil(10 * Math.pow(Math.pow(50, 1.0 / 99), getLevel() - 1) * (group == null ? 1 : group.getShinsu()) * (0.8 + random.nextDouble() * 0.4));
    }

    default double getInitialResistance(Random random) {
        Group group = getGroup();
        return Math.pow(Math.pow(10, 1.0 / 99), getLevel() - 1) * (group == null ? 1 : group.getResistance()) * (0.8 + random.nextDouble() * 0.4);
    }

    default double getInitialTension(Random random) {
        Group group = getGroup();
        return Math.pow(Math.pow(10, 1.0 / 99), getLevel() - 1) * (group == null ? 1 : group.getTension()) * (0.8 + random.nextDouble() * 0.4);
    }

    default void initializeStats(IServerWorld world) {
        ShinsuStats stats = getShinsuStats();
        Random random = world.getRandom();
        stats.setMaxShinsu(getInitialMaxShinsu(random));
        stats.setResistance(getInitialResistance(random));
        stats.setTension(getInitialTension(random));
    }

    default void initializeQuality(IServerWorld world) {
        ShinsuQualityData stats = getShinsuQualityData();
        stats.setAttribute(getInitialAttribute(world.getRandom()));
        stats.setShape(getInitialShape(world.getRandom()));
    }

    default void initialize(IServerWorld world) {
        Random random = world.getRandom();
        setGroup(getInitialGroup(random));
        initializeStats(world);
        initializeQuality(world);
    }

    default void shinsuTick(ServerWorld world) {
        getShinsuTechniqueData().tick(world);
    }

    default double getPreferredAttributeChance() {
        return 0.75;
    }

    @Nullable
    default ShinsuAttribute getInitialAttribute(Random random) {
        int level = getLevel();
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
        int level = getLevel();
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

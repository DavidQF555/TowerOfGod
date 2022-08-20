package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

import java.util.Collection;

public interface ShinsuQualityEffect<T extends RayTraceResult> {

    static <T extends RayTraceResult> CombinationQualityEffect<T> combination(Collection<ShinsuQualityEffect<T>> all) {
        return new CombinationQualityEffect<>(all);
    }

    void apply(Entity user, T clip);

}

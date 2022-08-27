package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

import java.util.Collection;

public interface ShinsuAttributeEffect<T extends RayTraceResult> {

    static <T extends RayTraceResult> CombinationAttributeEffect<T> combination(Collection<ShinsuAttributeEffect<T>> all) {
        return new CombinationAttributeEffect<>(all);
    }

    void apply(Entity user, T clip);

}

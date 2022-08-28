package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

import java.util.Collection;

public interface ShinsuAttributeEffect<T extends HitResult> {

    static <T extends HitResult> CombinationAttributeEffect<T> combination(Collection<ShinsuAttributeEffect<T>> all) {
        return new CombinationAttributeEffect<>(all);
    }

    void apply(Entity user, T clip);

}

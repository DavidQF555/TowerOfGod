package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

import java.util.Collection;

public class CombinationQualityEffect<T extends RayTraceResult> implements ShinsuQualityEffect<T> {

    private final Collection<ShinsuQualityEffect<T>> effects;

    public CombinationQualityEffect(Collection<ShinsuQualityEffect<T>> effects) {
        this.effects = effects;
    }

    @Override
    public void apply(Entity user, T clip) {
        effects.forEach(effect -> effect.apply(user, clip));
    }

}

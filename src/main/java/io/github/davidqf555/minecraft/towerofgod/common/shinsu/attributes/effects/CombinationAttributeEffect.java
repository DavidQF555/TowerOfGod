package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

import java.util.Collection;

public class CombinationAttributeEffect<T extends HitResult> implements ShinsuAttributeEffect<T> {

    private final Collection<ShinsuAttributeEffect<T>> effects;

    public CombinationAttributeEffect(Collection<ShinsuAttributeEffect<T>> effects) {
        this.effects = effects;
    }

    @Override
    public void apply(Entity user, T clip) {
        effects.forEach(effect -> effect.apply(user, clip));
    }

}

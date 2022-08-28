package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class SummonAttributeEffect<T extends HitResult, M extends Entity> implements ShinsuAttributeEffect<T> {

    private final Function<Level, M> factory;

    public SummonAttributeEffect(Function<Level, M> factory) {
        this.factory = factory;
    }

    @Override
    public void apply(Entity user, T clip) {
        M entity = factory.apply(user.level);
        Vec3 pos = clip.getLocation();
        entity.setPos(pos.x(), pos.y(), pos.z());
        user.level.addFreshEntity(entity);
    }

}

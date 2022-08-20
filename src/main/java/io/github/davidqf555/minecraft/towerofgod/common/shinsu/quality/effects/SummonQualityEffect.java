package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Function;

public class SummonQualityEffect<T extends RayTraceResult, M extends Entity> implements ShinsuQualityEffect<T> {

    private final Function<World, M> factory;

    public SummonQualityEffect(Function<World, M> factory) {
        this.factory = factory;
    }

    @Override
    public void apply(Entity user, T clip) {
        M entity = factory.apply(user.world);
        Vector3d pos = clip.getHitVec();
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        user.world.addEntity(entity);
    }

}

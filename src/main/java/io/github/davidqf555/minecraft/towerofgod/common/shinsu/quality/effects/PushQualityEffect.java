package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;

public class PushQualityEffect<T extends RayTraceResult> implements ShinsuQualityEffect<T> {

    private final double radius;
    private final Function<Double, Double> magnitude;

    public PushQualityEffect(double radius, Function<Double, Double> magnitude) {
        this.radius = radius;
        this.magnitude = magnitude;
    }

    @Override
    public void apply(Entity user, T clip) {
        Vector3d center = clip.getHitVec();
        AxisAlignedBB box = new AxisAlignedBB(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
        for (Entity target : user.world.getEntitiesWithinAABBExcludingEntity(user, box)) {
            Vector3d dir = target.getPositionVec().subtract(center.getX(), center.getY(), center.getZ());
            double length = dir.length();
            if (length <= radius) {
                double magnitude = this.magnitude.apply(length);
                Vector3d vec = dir.normalize().scale(magnitude);
                target.addVelocity(vec.x, vec.y, vec.z);
            }
        }
    }

}

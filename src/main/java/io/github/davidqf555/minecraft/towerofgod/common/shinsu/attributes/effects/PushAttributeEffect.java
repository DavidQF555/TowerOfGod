package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;

public class PushAttributeEffect<T extends RayTraceResult> implements ShinsuAttributeEffect<T> {

    private final double radius;
    private final Function<Double, Double> magnitude;

    public PushAttributeEffect(double radius, Function<Double, Double> magnitude) {
        this.radius = radius;
        this.magnitude = magnitude;
    }

    @Override
    public void apply(Entity user, T clip) {
        Vector3d center = clip.getLocation();
        AxisAlignedBB box = new AxisAlignedBB(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
        for (Entity target : user.level.getEntities(user, box)) {
            Vector3d dir = target.position().subtract(center.x(), center.y(), center.z());
            double length = dir.length();
            if (length <= radius) {
                double magnitude = this.magnitude.apply(length);
                Vector3d vec = dir.normalize().scale(magnitude);
                target.push(vec.x, vec.y, vec.z);
                target.hurtMarked = true;
            }
        }
    }

}

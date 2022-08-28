package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class PushAttributeEffect<T extends HitResult> implements ShinsuAttributeEffect<T> {

    private final double radius;
    private final Function<Double, Double> magnitude;

    public PushAttributeEffect(double radius, Function<Double, Double> magnitude) {
        this.radius = radius;
        this.magnitude = magnitude;
    }

    @Override
    public void apply(Entity user, T clip) {
        Vec3 center = clip.getLocation();
        AABB box = new AABB(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
        for (Entity target : user.level.getEntities(user, box)) {
            Vec3 dir = target.position().subtract(center.x(), center.y(), center.z());
            double length = dir.length();
            if (length <= radius) {
                double magnitude = this.magnitude.apply(length);
                Vec3 vec = dir.normalize().scale(magnitude);
                target.push(vec.x, vec.y, vec.z);
                target.hurtMarked = true;
            }
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class KnockbackAttributeEffect implements ShinsuAttributeEffect<EntityRayTraceResult> {

    private final double magnitude;

    public KnockbackAttributeEffect(double magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public void apply(Entity user, EntityRayTraceResult clip) {
        Entity target = clip.getEntity();
        Vector3d push = clip.getLocation().normalize().scale(magnitude);
        target.push(push.x(), push.y(), push.z());
        target.hurtMarked = true;
    }

}
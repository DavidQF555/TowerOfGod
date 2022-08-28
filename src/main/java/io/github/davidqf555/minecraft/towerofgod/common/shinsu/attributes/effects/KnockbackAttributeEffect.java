package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class KnockbackAttributeEffect implements ShinsuAttributeEffect<EntityHitResult> {

    private final double magnitude;

    public KnockbackAttributeEffect(double magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public void apply(Entity user, EntityHitResult clip) {
        Entity target = clip.getEntity();
        Vec3 push = clip.getLocation().normalize().scale(magnitude);
        target.push(push.x(), push.y(), push.z());
        target.hurtMarked = true;
    }

}

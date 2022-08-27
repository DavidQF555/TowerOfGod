package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;

public class FireEntityAttributeEffect implements ShinsuAttributeEffect<EntityRayTraceResult> {

    private final int duration;

    public FireEntityAttributeEffect(int duration) {
        this.duration = duration;
    }

    @Override
    public void apply(Entity user, EntityRayTraceResult clip) {
        clip.getEntity().setSecondsOnFire(duration);
    }

}

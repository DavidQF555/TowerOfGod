package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;

public class FireEntityQualityEffect implements ShinsuQualityEffect<EntityRayTraceResult> {

    private final int duration;

    public FireEntityQualityEffect(int duration) {
        this.duration = duration;
    }

    @Override
    public void apply(Entity user, EntityRayTraceResult clip) {
        clip.getEntity().setFire(duration);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

public class FireEntityAttributeEffect implements ShinsuAttributeEffect<EntityHitResult> {

    private final int duration;

    public FireEntityAttributeEffect(int duration) {
        this.duration = duration;
    }

    @Override
    public void apply(Entity user, EntityHitResult clip) {
        clip.getEntity().setSecondsOnFire(duration);
    }

}

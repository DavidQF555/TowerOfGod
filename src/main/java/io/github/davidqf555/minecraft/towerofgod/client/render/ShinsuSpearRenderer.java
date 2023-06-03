package io.github.davidqf555.minecraft.towerofgod.client.render;

import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuSpearEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class ShinsuSpearRenderer extends SpearRenderer<ShinsuSpearEntity> {

    public ShinsuSpearRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected int getColor(ShinsuSpearEntity entity) {
        return ShinsuAttribute.getColor(entity.getAttribute());
    }

}

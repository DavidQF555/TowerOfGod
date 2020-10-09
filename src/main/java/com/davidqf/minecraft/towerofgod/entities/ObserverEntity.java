package com.davidqf.minecraft.towerofgod.entities;

import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObserverEntity extends FlyingDevice {

    public ObserverEntity(World worldIn) {
        super(RegistryHandler.OBSERVER_ENTITY.get(), worldIn);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.3)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3)
                .createMutableAttribute(Attributes.MAX_HEALTH, 10);
    }

    public static class Factory implements EntityType.IFactory<ObserverEntity> {
        @Nonnull
        @Override
        public ObserverEntity create(@Nullable EntityType<ObserverEntity> type, @Nonnull World world) {
            return new ObserverEntity(world);
        }
    }
}

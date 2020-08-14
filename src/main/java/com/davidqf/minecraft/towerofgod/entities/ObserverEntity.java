package com.davidqf.minecraft.towerofgod.entities;

import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObserverEntity extends FlyingDevice {

    public ObserverEntity(World worldIn, @Nullable LivingEntity owner) {
        super(RegistryHandler.OBSERVER_ENTITY.get(), worldIn, owner);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return ObserverEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233822_e_, 3)
                .func_233815_a_(Attributes.field_233821_d_, 3)
                .func_233815_a_(Attributes.field_233818_a_, 10);
    }

    public static class Factory implements EntityType.IFactory<ObserverEntity> {
        @Nonnull
        @Override
        public ObserverEntity create(@Nullable EntityType<ObserverEntity> type, @Nonnull World world) {
            return new ObserverEntity(world, null);
        }
    }
}

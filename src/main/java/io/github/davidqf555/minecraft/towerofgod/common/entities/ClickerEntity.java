package io.github.davidqf555.minecraft.towerofgod.common.entities;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ClickerEntity extends Entity {

    private static final int PARTICLES = 2;
    private static final int SPEED = 5;
    private static final int DURATION = 200;
    private static final EntityDataAccessor<String> ATTRIBUTE = SynchedEntityData.defineId(ClickerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SHAPE = SynchedEntityData.defineId(ClickerEntity.class, EntityDataSerializers.STRING);
    private int ticksLeft;

    public ClickerEntity(EntityType<ClickerEntity> type, Level world) {
        super(type, world);
        ticksLeft = DURATION;
    }

    @Override
    protected void defineSynchedData() {
        SynchedEntityData manager = getEntityData();
        manager.define(ATTRIBUTE, "");
        manager.define(SHAPE, ShinsuShapeRegistry.SWORD.getId().toString());
    }

    @Override
    public void tick() {
        yRotO = getYRot();
        setYRot(yRotO + SPEED);
        ParticleOptions particle = ShinsuAttribute.getParticles(getAttribute());
        for (int i = 0; i < PARTICLES; i++) {
            level.addParticle(particle, getRandomX(1), getRandomY(), getRandomZ(1), 0, 0, 0);
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            remove(RemovalReason.DISCARDED);
        }
        super.tick();
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(entityData.get(ATTRIBUTE)));
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        getEntityData().set(ATTRIBUTE, attribute == null ? "" : attribute.getId().toString());
    }

    public ShinsuShape getShape() {
        return ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(getEntityData().get(SHAPE)));
    }

    public void setShape(ShinsuShape shape) {
        entityData.set(SHAPE, shape.getId().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            ticksLeft = nbt.getInt("Duration");
        }
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
        if (nbt.contains("Shape", Tag.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Duration", ticksLeft);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            nbt.putString("Attribute", attribute.getId().toString());
        }
        nbt.putString("Shape", getShape().getId().toString());
    }

}

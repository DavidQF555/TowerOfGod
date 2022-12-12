package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DirectionalLightningBoltEntity extends LightningBolt {

    private static final EntityDataAccessor<Float> X = SynchedEntityData.defineId(DirectionalLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Y = SynchedEntityData.defineId(DirectionalLightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Z = SynchedEntityData.defineId(DirectionalLightningBoltEntity.class, EntityDataSerializers.FLOAT);

    public DirectionalLightningBoltEntity(EntityType<DirectionalLightningBoltEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        SynchedEntityData manager = getEntityData();
        manager.define(X, 0f);
        manager.define(Y, 0f);
        manager.define(Z, 0f);
    }

    public Vector3f getStart() {
        SynchedEntityData manager = getEntityData();
        return new Vector3f(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vector3fc pos) {
        SynchedEntityData manager = getEntityData();
        manager.set(X, pos.x());
        manager.set(Y, pos.y());
        manager.set(Z, pos.z());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("X", Tag.TAG_FLOAT) && compound.contains("Y", Tag.TAG_FLOAT) && compound.contains("Z", Tag.TAG_FLOAT)) {
            setStart(new Vector3f(compound.getFloat("X"), compound.getFloat("Y"), compound.getFloat("Z")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        Vector3f start = getStart();
        compound.putFloat("X", start.x());
        compound.putFloat("Y", start.y());
        compound.putFloat("Z", start.z());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class DirectionalLightningBoltEntity extends LightningBoltEntity {

    private static final DataParameter<Float> X = EntityDataManager.defineId(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Y = EntityDataManager.defineId(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Z = EntityDataManager.defineId(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);

    public DirectionalLightningBoltEntity(EntityType<DirectionalLightningBoltEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        EntityDataManager manager = getEntityData();
        manager.define(X, 0f);
        manager.define(Y, 0f);
        manager.define(Z, 0f);
    }

    public Vector3f getStart() {
        EntityDataManager manager = getEntityData();
        return new Vector3f(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vector3f pos) {
        EntityDataManager manager = getEntityData();
        manager.set(X, pos.x());
        manager.set(Y, pos.y());
        manager.set(Z, pos.z());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains("X", Constants.NBT.TAG_FLOAT) && compound.contains("Y", Constants.NBT.TAG_FLOAT) && compound.contains("Z", Constants.NBT.TAG_FLOAT)) {
            setStart(new Vector3f(compound.getFloat("X"), compound.getFloat("Y"), compound.getFloat("Z")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        Vector3f start = getStart();
        compound.putFloat("X", start.x());
        compound.putFloat("Y", start.y());
        compound.putFloat("Z", start.z());
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

}

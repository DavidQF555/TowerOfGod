package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
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

    private static final DataParameter<Float> X = EntityDataManager.createKey(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Y = EntityDataManager.createKey(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Z = EntityDataManager.createKey(DirectionalLightningBoltEntity.class, DataSerializers.FLOAT);
    private int level;

    public DirectionalLightningBoltEntity(World world, int level) {
        super(RegistryHandler.LIGHTNING_PROJECTILE_ENTITY.get(), world);
        this.level = level;
    }

    @Override
    protected void registerData() {
        EntityDataManager manager = getDataManager();
        manager.register(X, 0f);
        manager.register(Y, 0f);
        manager.register(Z, 0f);
    }

    public Vector3f getStart() {
        EntityDataManager manager = getDataManager();
        return new Vector3f(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vector3f pos) {
        EntityDataManager manager = getDataManager();
        manager.set(X, pos.getX());
        manager.set(Y, pos.getY());
        manager.set(Z, pos.getZ());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        if (compound.contains("X", Constants.NBT.TAG_FLOAT) && compound.contains("Y", Constants.NBT.TAG_FLOAT) && compound.contains("Z", Constants.NBT.TAG_FLOAT)) {
            setStart(new Vector3f(compound.getFloat("X"), compound.getFloat("Y"), compound.getFloat("Z")));
        }
        if (compound.contains("Level", Constants.NBT.TAG_INT)) {
            level = compound.getInt("Level");
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        Vector3f start = getStart();
        compound.putFloat("X", start.getX());
        compound.putFloat("Y", start.getY());
        compound.putFloat("Z", start.getZ());
        compound.putInt("Level", level);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    public static class Factory implements EntityType.IFactory<DirectionalLightningBoltEntity> {

        @Override
        public DirectionalLightningBoltEntity create(EntityType<DirectionalLightningBoltEntity> type, World world) {
            return new DirectionalLightningBoltEntity(world, 1);
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ClickerEntity extends Entity {

    private static final int PARTICLES = 2;
    private static final int SPEED = 5;
    private static final int DURATION = 200;
    private static final DataParameter<String> QUALITY = EntityDataManager.defineId(ClickerEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> SHAPE = EntityDataManager.defineId(ClickerEntity.class, DataSerializers.STRING);
    private int ticksLeft;

    public ClickerEntity(EntityType<ClickerEntity> type, World world) {
        super(type, world);
        ticksLeft = DURATION;
    }

    @Override
    protected void defineSynchedData() {
        EntityDataManager manager = getEntityData();
        manager.define(QUALITY, "");
        manager.define(SHAPE, ShinsuShapeRegistry.SWORD.getId().toString());
    }

    @Override
    public void tick() {
        yRotO = yRot;
        yRot += SPEED;
        IParticleData particle = ShinsuQuality.getParticles(getQuality());
        for (int i = 0; i < PARTICLES; i++) {
            level.addParticle(particle, getRandomX(1), getRandomY(), getRandomZ(1), 0, 0, 0);
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            remove();
        }
        super.tick();
    }

    @Nullable
    public ShinsuQuality getQuality() {
        return ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(entityData.get(QUALITY)));
    }

    public void setQuality(@Nullable ShinsuQuality quality) {
        entityData.set(QUALITY, quality == null ? "" : quality.getRegistryName().toString());
    }

    public ShinsuShape getShape() {
        return ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(entityData.get(SHAPE)));
    }

    public void setShape(ShinsuShape shape) {
        entityData.set(SHAPE, shape.getRegistryName().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            ticksLeft = nbt.getInt("Duration");
        }
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            setQuality(ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Quality"))));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            setShape(ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape"))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.putInt("Duration", ticksLeft);
        ShinsuQuality quality = getQuality();
        if (quality != null) {
            nbt.putString("Quality", quality.getRegistryName().toString());
        }
        nbt.putString("Shape", getShape().getRegistryName().toString());
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

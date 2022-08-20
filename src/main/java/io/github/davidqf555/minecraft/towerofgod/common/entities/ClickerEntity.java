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
    private static final DataParameter<String> QUALITY = EntityDataManager.createKey(ClickerEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> SHAPE = EntityDataManager.createKey(ClickerEntity.class, DataSerializers.STRING);
    private int ticksLeft;

    public ClickerEntity(EntityType<ClickerEntity> type, World world) {
        super(type, world);
        ticksLeft = DURATION;
    }

    @Override
    protected void registerData() {
        EntityDataManager manager = getDataManager();
        manager.register(QUALITY, "");
        manager.register(SHAPE, ShinsuShapeRegistry.SWORD.getId().toString());
    }

    @Override
    public void tick() {
        prevRotationYaw = rotationYaw;
        rotationYaw += SPEED;
        IParticleData particle = ShinsuQuality.getParticles(getQuality());
        for (int i = 0; i < PARTICLES; i++) {
            world.addParticle(particle, getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            remove();
        }
        super.tick();
    }

    @Nullable
    public ShinsuQuality getQuality() {
        return ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(dataManager.get(QUALITY)));
    }

    public void setQuality(@Nullable ShinsuQuality quality) {
        dataManager.set(QUALITY, quality == null ? "" : quality.getRegistryName().toString());
    }

    public ShinsuShape getShape() {
        return ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(dataManager.get(SHAPE)));
    }

    public void setShape(ShinsuShape shape) {
        dataManager.set(SHAPE, shape.getRegistryName().toString());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
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
    public void writeAdditional(CompoundNBT nbt) {
        nbt.putInt("Duration", ticksLeft);
        ShinsuQuality quality = getQuality();
        if (quality != null) {
            nbt.putString("Quality", quality.getRegistryName().toString());
        }
        nbt.putString("Shape", getShape().getRegistryName().toString());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

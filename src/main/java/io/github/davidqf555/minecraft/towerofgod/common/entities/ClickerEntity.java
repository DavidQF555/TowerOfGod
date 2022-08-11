package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
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

    public ClickerEntity(World worldIn) {
        super(EntityRegistry.CLICKER.get(), worldIn);
        ticksLeft = DURATION;
    }

    @Override
    protected void registerData() {
        EntityDataManager manager = getDataManager();
        manager.register(QUALITY, ShinsuQuality.NONE.name());
        manager.register(SHAPE, ShinsuShape.NONE.name());
    }

    @Override
    public void tick() {
        prevRotationYaw = rotationYaw;
        rotationYaw += SPEED;
        IParticleData particle = getQuality().getParticleType();
        for (int i = 0; i < PARTICLES; i++) {
            world.addParticle(particle, getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            remove();
        }
        super.tick();
    }

    public ShinsuQuality getQuality() {
        return ShinsuQuality.valueOf(dataManager.get(QUALITY));
    }

    public void setQuality(ShinsuQuality quality) {
        dataManager.set(QUALITY, quality.name());
    }

    public ShinsuShape getShape() {
        return ShinsuShape.valueOf(dataManager.get(SHAPE));
    }

    public void setShape(ShinsuShape shape) {
        dataManager.set(SHAPE, shape.name());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            ticksLeft = nbt.getInt("Duration");
        }
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            setQuality(ShinsuQuality.valueOf(nbt.getString("Quality")));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            setShape(ShinsuShape.valueOf(nbt.getString("Shape")));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        nbt.putInt("Duration", ticksLeft);
        nbt.putString("Quality", getQuality().name());
        nbt.putString("Shape", getShape().name());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements EntityType.IFactory<ClickerEntity> {
        @Override
        public ClickerEntity create(@Nullable EntityType<ClickerEntity> type, World world) {
            return new ClickerEntity(world);
        }
    }
}

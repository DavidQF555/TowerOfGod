package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.instances.ShinsuTechniqueInstance;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuEntity extends DamagingProjectileEntity {

    private static final int PARTICLES = 3;
    private static final float DAMAGE = 5;
    private static final DataParameter<String> QUALITY = EntityDataManager.createKey(ShinsuEntity.class, DataSerializers.STRING);
    private UUID technique;
    private BlockRayTraceResult latestHit;

    public ShinsuEntity(World world) {
        super(EntityRegistry.SHINSU.get(), world);
        technique = null;
        latestHit = null;
    }

    @Override
    public void registerData() {
        super.registerData();
        dataManager.register(QUALITY, ShinsuQuality.NONE.name());
    }

    @Override
    public boolean isFireballFiery() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public IParticleData getParticle() {
        return getQuality().getParticleType();
    }

    public ShinsuQuality getQuality() {
        return ShinsuQuality.valueOf(dataManager.get(QUALITY));
    }

    public void setQuality(ShinsuQuality quality) {
        dataManager.set(QUALITY, quality.name());
    }

    @Override
    public void setMotion(Vector3d motionIn) {
        super.setMotion(motionIn);
        double length = motionIn.length();
        accelerationX = motionIn.getX() / length * 0.1;
        accelerationY = motionIn.getY() / length * 0.1;
        accelerationZ = motionIn.getZ() / length * 0.1;
    }

    @Override
    public void onEntityHit(EntityRayTraceResult rayTraceResult) {
        super.onEntityHit(rayTraceResult);
        if (world instanceof ServerWorld) {
            Entity shooter = getShooter();
            Entity target = rayTraceResult.getEntity();
            ShinsuQuality quality = getQuality();
            float damage = (float) ((shooter == null) ? quality.getDamage() * DAMAGE : ShinsuStats.getNetResistance((ServerWorld) world, shooter, target) * quality.getDamage() * DAMAGE) / 8;
            quality.applyEntityEffect(this, rayTraceResult);
            target.attackEntityFrom(quality.getSource(), damage);
        }
        remove();
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = getShooter();
        if (technique != null && shooter != null) {
            return ShinsuTechniqueInstance.get(shooter, technique);
        }
        return null;
    }

    public void setTechnique(ShinsuTechniqueInstance technique) {
        this.technique = technique.getID();
    }

    @Override
    public void tick() {
        if (technique != null && getTechnique() == null) {
            remove();
        }
        for (int i = 0; i < PARTICLES; i++) {
            world.addParticle(getParticle(), getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult rayTraceResult) {
        latestHit = rayTraceResult;
        super.func_230299_a_(rayTraceResult);
        remove();
    }

    @Override
    public void onRemovedFromWorld() {
        if (world instanceof ServerWorld) {
            Vector3d motion = getMotion();
            getQuality().applyBlockEffect(this, latestHit == null || isAirBorne ? new BlockRayTraceResult(motion, Direction.getFacingFromVector(motion.x, motion.y, motion.z), getPosition(), true) : latestHit);
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            technique = nbt.getUniqueId("Technique");
        }
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            setQuality(ShinsuQuality.valueOf(nbt.getString("Quality")));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (technique != null) {
            nbt.putUniqueId("Technique", technique);
        }
        nbt.putString("Quality", getQuality().name());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements EntityType.IFactory<ShinsuEntity> {
        @Override
        public ShinsuEntity create(@Nullable EntityType<ShinsuEntity> type, World world) {
            return new ShinsuEntity(world);
        }
    }
}

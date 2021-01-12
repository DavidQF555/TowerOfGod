package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
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

    private static final String TAG_KEY = TowerOfGod.MOD_ID + ".shinsuentity";
    private static final int PARTICLES = 3;
    private static final float DAMAGE = 5;
    private static final DataParameter<String> QUALITY = EntityDataManager.createKey(ShinsuEntity.class, DataSerializers.STRING);
    private UUID technique;
    private int level;
    private BlockRayTraceResult latestHit;

    public ShinsuEntity(World world, @Nullable LivingEntity shooter, ShinsuQuality quality, @Nullable ShinsuTechniqueInstance technique, int level, double x, double y, double z, double dX, double dY, double dZ) {
        super(RegistryHandler.SHINSU_ENTITY.get(), x, y, z, dX, dY, dZ, world);
        this.technique = technique == null ? null : technique.getID();
        setShooter(shooter);
        setQuality(quality);
        this.level = level;
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
    public IParticleData getParticle() {
        return getQuality().getParticleType();
    }

    public ShinsuQuality getQuality() {
        return ShinsuQuality.get(dataManager.get(QUALITY));
    }

    public void setQuality(ShinsuQuality quality) {
        dataManager.set(QUALITY, quality.name());
    }

    @Override
    public void onEntityHit(EntityRayTraceResult rayTraceResult) {
        super.onEntityHit(rayTraceResult);
        Entity e = rayTraceResult.getEntity();
        Entity s = func_234616_v_();
        if (e instanceof LivingEntity && s instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) e;
            ShinsuQuality quality = getQuality();
            float damage = (float) (ShinsuTechniqueInstance.getTotalResistance((LivingEntity) s, target) * level * quality.getDamage() * DAMAGE) / 3;
            quality.applyEntityEffect(this, rayTraceResult);
            target.attackEntityFrom(quality.getSource(), damage);
        }
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = func_234616_v_();
        if (technique != null && shooter != null) {
            return ShinsuTechniqueInstance.get(shooter, technique);
        }
        return null;
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
        ShinsuTechniqueInstance technique = getTechnique();
        if (world instanceof ServerWorld) {
            Vector3d motion = getMotion();
            getQuality().applyBlockEffect(this, latestHit == null || isAirBorne ? new BlockRayTraceResult(motion, Direction.getFacingFromVector(motion.x, motion.y, motion.z), getPosition(), true) : latestHit);
            if (technique != null) {
                technique.remove((ServerWorld) world);
            }
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT e = (CompoundNBT) nbt.get(TAG_KEY);
            technique = e.contains("Technique", Constants.NBT.TAG_COMPOUND) ? e.getUniqueId("Technique") : null;
            setQuality(ShinsuQuality.get(e.getString("Quality")));
            level = e.getInt("Level");
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        CompoundNBT e = new CompoundNBT();
        if (technique != null) {
            e.putUniqueId("Technique", technique);
        }
        e.putString("Quality", getQuality().name());
        e.putInt("Level", level);
        nbt.put(TAG_KEY, e);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements EntityType.IFactory<ShinsuEntity> {
        @Override
        public ShinsuEntity create(@Nullable EntityType<ShinsuEntity> type, World world) {
            return new ShinsuEntity(world, null, ShinsuQuality.NONE, null, 1, 0, 0, 0, 0, 0, 0);
        }
    }
}

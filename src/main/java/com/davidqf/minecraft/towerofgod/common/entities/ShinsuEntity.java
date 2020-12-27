package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShinsuEntity extends DamagingProjectileEntity {

    private static final String TAG_KEY = "towerofgod.shinsuentity";
    private static final int PARTICLES = 3;
    private static final float DAMAGE = 5;
    private static final DataParameter<String> QUALITY = EntityDataManager.createKey(ShinsuEntity.class, DataSerializers.STRING);
    private int level;

    public ShinsuEntity(@Nonnull World world, @Nullable LivingEntity shooter, @Nonnull ShinsuQuality quality, int level, double x, double y, double z, double dX, double dY, double dZ) {
        super(RegistryHandler.SHINSU_ENTITY.get(), x, y, z, dX, dY, dZ, world);
        setShooter(shooter);
        setQuality(quality);
        this.level = level;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(QUALITY, ShinsuQuality.NONE.name());
    }

    @Override
    public boolean isFireballFiery() {
        return false;
    }

    @Nonnull
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
    public void onEntityHit(@Nonnull EntityRayTraceResult rayTraceResult) {
        super.onEntityHit(rayTraceResult);
        Entity e = rayTraceResult.getEntity();
        Entity s = func_234616_v_();
        if (e instanceof LivingEntity && s instanceof LivingEntity) {
            boolean remove = true;
            LivingEntity target = (LivingEntity) e;
            float damage = (float) (DAMAGE * ShinsuTechniqueInstance.getTotalResistance((LivingEntity) s, target) * level / 3.0);
            DamageSource source = DamageSource.MAGIC;
            ShinsuQuality quality = getQuality();
            switch (quality) {
                case ICE:
                    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 2, true, false, false));
                    break;
                case FIRE:
                    source = DamageSource.ON_FIRE;
                    target.setFire(7);
                    break;
                case WIND:
                    Vector3d vec = target.getPositionVec().subtract(rayTraceResult.getHitVec()).normalize().mul(3, 3, 3);
                    target.addVelocity(vec.getX(), vec.getY(), vec.getZ());
                    break;
                case PLANT:
                    source = DamageSource.CACTUS;
                    target.addPotionEffect(new EffectInstance(Effects.POISON, 140, 2, true, false, false));
                    break;
                case STONE:
                    source = DamageSource.FALLING_BLOCK;
                    damage += 3;
                case CRYSTAL:
                    damage += 2;
                    remove = false;
                    break;
                case LIGHTNING:
                    source = DamageSource.LIGHTNING_BOLT;
                    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 3, true, false, false));
                    target.setFire(3);
                    break;
            }
            target.attackEntityFrom(source, damage);
            if (remove) {
                remove();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        for (int i = 0; i < PARTICLES; i++) {
            world.addParticle(getParticle(), getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
        }
    }

    @Override
    protected void func_230299_a_(@Nonnull BlockRayTraceResult rayTraceResult) {
        super.func_230299_a_(rayTraceResult);
        remove();
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains(TAG_KEY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT e = (CompoundNBT) nbt.get(TAG_KEY);
            setQuality(ShinsuQuality.get(e.getString("Quality")));
            level = e.getInt("Level");
        }
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT nbt) {
        super.writeAdditional(nbt);
        CompoundNBT e = new CompoundNBT();
        e.putString("Quality", getQuality().name());
        e.putInt("Level", level);
        nbt.put(TAG_KEY, e);
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements EntityType.IFactory<ShinsuEntity> {
        @Nonnull
        @Override
        public ShinsuEntity create(@Nullable EntityType<ShinsuEntity> type, @Nonnull World world) {
            return new ShinsuEntity(world, null, ShinsuQuality.NONE, 1, 0, 0, 0, 0, 0, 0);
        }
    }
}

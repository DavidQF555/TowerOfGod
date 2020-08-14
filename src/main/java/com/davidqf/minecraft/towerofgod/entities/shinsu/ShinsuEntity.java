package com.davidqf.minecraft.towerofgod.entities.shinsu;

import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
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
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
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

    public ShinsuEntity(@Nonnull World world, @Nullable LivingEntity shooter, @Nonnull ShinsuQuality quality, int level) {
        super(RegistryHandler.SHINSU_ENTITY.get(), world);
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
            float damage = (float) (DAMAGE * ShinsuTechnique.getTotalResistance((LivingEntity) s, target) * level / 3.0);
            DamageSource source = DamageSource.MAGIC;
            ShinsuQuality quality = getQuality();
            if (quality != null) {
                if (quality == ShinsuQuality.ICE) {
                    target.addPotionEffect(new EffectInstance(Effect.get(2), 60, 2, true, false, false));
                } else if (quality == ShinsuQuality.FIRE) {
                    source = DamageSource.ON_FIRE;
                    target.setFire(7);
                } else if (quality == ShinsuQuality.WIND) {
                    Vector3d vec = target.getPositionVec().subtract(rayTraceResult.getHitVec()).normalize().mul(3, 3, 3);
                    target.addVelocity(vec.getX(), vec.getY(), vec.getZ());
                } else if (quality == ShinsuQuality.PLANT) {
                    source = DamageSource.CACTUS;
                    target.addPotionEffect(new EffectInstance(Effect.get(19), 140, 2, true, false, false));
                } else if (quality == ShinsuQuality.STONE) {
                    source = DamageSource.FALLING_BLOCK;
                    damage += 3;
                } else if (quality == ShinsuQuality.CRYSTAL) {
                    damage += 2;
                    remove = false;
                } else if (quality == ShinsuQuality.LIGHTNING) {
                    source = DamageSource.LIGHTNING_BOLT;
                    target.addPotionEffect(new EffectInstance(Effect.get(2), 20, 3, true, false, false));
                    target.setFire(3);
                }
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
        if (ticksExisted >= ShinsuTechniques.SHINSU_BLAST.getDuration()) {
            remove();
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
            return new ShinsuEntity(world, null, ShinsuQuality.NONE, 1);
        }
    }
}

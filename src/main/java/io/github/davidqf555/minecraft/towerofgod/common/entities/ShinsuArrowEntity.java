package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
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

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShinsuArrowEntity extends AbstractArrowEntity {

    private static final int PARTICLES = 2;
    private static final DataParameter<String> QUALITY = EntityDataManager.createKey(ShinsuArrowEntity.class, DataSerializers.STRING);
    private UUID technique;
    private BlockRayTraceResult latestHit;

    public ShinsuArrowEntity(World worldIn, @Nullable UUID technique) {
        super(RegistryHandler.SHINSU_ARROW_ENTITY.get(), worldIn);
        this.technique = technique;
        this.latestHit = null;
    }

    @Override
    public void registerData() {
        super.registerData();
        dataManager.register(QUALITY, ShinsuQuality.NONE.name());
    }

    @Override
    public void tick() {
        if (technique != null && getTechnique() == null) {
            remove();
        }
        IParticleData particle = getQuality().getParticleType();
        int particles = PARTICLES;
        if (inGround) {
            particles = (int) Math.ceil(particles / 2.0);
        }
        for (int i = 0; i < particles; i++) {
            world.addParticle(particle, getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult rayTraceResult) {
        super.onEntityHit(rayTraceResult);
        ShinsuQuality quality = getQuality();
        quality.applyEntityEffect(this, rayTraceResult);
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult rayTraceResult) {
        latestHit = rayTraceResult;
        super.func_230299_a_(rayTraceResult);
    }

    @Override
    protected void func_225516_i_() {
        if (timeInGround >= 0) {
            remove();
        }
    }

    @Override
    public void onRemovedFromWorld() {
        if (world instanceof ServerWorld) {
            Vector3d motion = getMotion();
            getQuality().applyBlockEffect(this, latestHit == null || isAirBorne ? new BlockRayTraceResult(motion, Direction.getFacingFromVector(motion.x, motion.y, motion.z), getPosition(), true) : latestHit);
        }
        super.onRemovedFromWorld();
    }

    public ShinsuQuality getQuality() {
        return ShinsuQuality.valueOf(dataManager.get(QUALITY));
    }

    public void setQuality(ShinsuQuality quality) {
        ShinsuQuality original = getQuality();
        setDamage(getDamage() * quality.getDamage() / original.getDamage());
        dataManager.set(QUALITY, quality.name());
    }

    @Override
    public void setShooter(@Nullable Entity entityIn) {
        super.setShooter(entityIn);
        pickupStatus = PickupStatus.DISALLOWED;
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = getShooter();
        if (technique != null && shooter != null) {
            return ShinsuTechniqueInstance.get(shooter, technique);
        }
        return null;
    }

    public void setTechnique(@Nullable UUID technique) {
        this.technique = technique;
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean getIsCritical() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            setQuality(ShinsuQuality.valueOf(nbt.getString("Quality")));
        }
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            setTechnique(nbt.getUniqueId("Technique"));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putString("Quality", getQuality().name());
        if (technique != null) {
            nbt.putUniqueId("Technique", technique);
        }
    }

    public static class Factory implements EntityType.IFactory<ShinsuArrowEntity> {

        @Override
        public ShinsuArrowEntity create(@Nullable EntityType<ShinsuArrowEntity> type, World world) {
            return new ShinsuArrowEntity(world, null);
        }
    }
}

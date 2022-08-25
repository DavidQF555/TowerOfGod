package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
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
import net.minecraft.util.ResourceLocation;
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
    private static final DataParameter<String> QUALITY = EntityDataManager.defineId(ShinsuArrowEntity.class, DataSerializers.STRING);
    private UUID technique;
    private BlockRayTraceResult latestHit;

    public ShinsuArrowEntity(EntityType<ShinsuArrowEntity> type, World world) {
        this(type, world, null);
    }

    public ShinsuArrowEntity(EntityType<ShinsuArrowEntity> type, World world, @Nullable UUID technique) {
        super(type, world);
        this.technique = technique;
        this.latestHit = null;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(QUALITY, "");
    }

    @Override
    public void tick() {
        if (technique != null && getTechnique() == null) {
            remove();
        }
        IParticleData particle = ShinsuQuality.getParticles(getQuality());
        int particles = PARTICLES;
        if (inGround) {
            particles = (int) Math.ceil(particles / 2.0);
        }
        for (int i = 0; i < particles; i++) {
            level.addParticle(particle, getRandomX(1), getRandomY(), getRandomZ(1), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        ShinsuQuality quality = getQuality();
        if (quality != null) {
            quality.applyEntityEffect(this, rayTraceResult);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult rayTraceResult) {
        latestHit = rayTraceResult;
        super.onHitBlock(rayTraceResult);
    }

    @Override
    protected void tickDespawn() {
        if (inGroundTime >= 0) {
            remove();
        }
    }

    @Override
    public void onRemovedFromWorld() {
        if (level instanceof ServerWorld) {
            ShinsuQuality quality = getQuality();
            if (quality != null) {
                Vector3d motion = getDeltaMovement();
                quality.applyBlockEffect(this, latestHit == null || hasImpulse ? new BlockRayTraceResult(motion, Direction.getNearest(motion.x, motion.y, motion.z), blockPosition(), true) : latestHit);
            }
        }
        super.onRemovedFromWorld();
    }

    @Nullable
    public ShinsuQuality getQuality() {
        return ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(getEntityData().get(QUALITY)));
    }

    public void setQuality(@Nullable ShinsuQuality quality) {
        ShinsuQuality original = getQuality();
        setBaseDamage(getBaseDamage() * (quality == null ? 1 : quality.getDamage()) / (original == null ? 1 : original.getDamage()));
        getEntityData().set(QUALITY, quality == null ? "" : quality.getRegistryName().toString());
    }

    @Override
    public void setOwner(@Nullable Entity entityIn) {
        super.setOwner(entityIn);
        pickup = PickupStatus.DISALLOWED;
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = getOwner();
        if (technique != null && shooter != null) {
            return ShinsuTechniqueInstance.get(shooter, technique);
        }
        return null;
    }

    public void setTechnique(@Nullable UUID technique) {
        this.technique = technique;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isCritArrow() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Quality", Constants.NBT.TAG_STRING)) {
            setQuality(ShinsuQualityRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Quality"))));
        }
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            setTechnique(nbt.getUUID("Technique"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        ShinsuQuality quality = getQuality();
        if (quality != null) {
            nbt.putString("Quality", quality.getRegistryName().toString());
        }
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
    }

}

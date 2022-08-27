package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuEntity extends DamagingProjectileEntity {

    private static final int PARTICLES = 3;
    private static final float DAMAGE = 0.625f;
    private static final DataParameter<String> ATTRIBUTE = EntityDataManager.defineId(ShinsuEntity.class, DataSerializers.STRING);
    private UUID technique;
    private BlockRayTraceResult latestHit;

    public ShinsuEntity(EntityType<ShinsuEntity> type, World world) {
        super(type, world);
        technique = null;
        latestHit = null;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ATTRIBUTE, "");
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public IParticleData getTrailParticle() {
        return ShinsuAttribute.getParticles(getAttribute());
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(getEntityData().get(ATTRIBUTE)));
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        getEntityData().set(ATTRIBUTE, attribute == null ? "" : attribute.getRegistryName().toString());
    }

    @Override
    public void setDeltaMovement(Vector3d motionIn) {
        super.setDeltaMovement(motionIn);
        double length = motionIn.length();
        xPower = motionIn.x() / length * 0.1;
        yPower = motionIn.y() / length * 0.1;
        zPower = motionIn.z() / length * 0.1;
    }

    @Override
    public void onHitEntity(EntityRayTraceResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        if (level instanceof ServerWorld) {
            Entity shooter = getOwner();
            Entity target = rayTraceResult.getEntity();
            float damage = DAMAGE;
            if (shooter != null) {
                damage *= ShinsuStats.getNetResistance(shooter, target);
            }
            ShinsuAttribute attribute = getAttribute();
            if (attribute != null) {
                damage *= attribute.getDamage();
                attribute.applyEntityEffect(this, rayTraceResult);
            }
            target.hurt(ShinsuAttribute.getDamageSource(attribute), damage);
        }
        remove();
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = getOwner();
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
            level.addParticle(getTrailParticle(), getRandomX(1), getRandomY(), getRandomZ(1), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult rayTraceResult) {
        latestHit = rayTraceResult;
        super.onHitBlock(rayTraceResult);
        remove();
    }

    @Override
    public void onRemovedFromWorld() {
        if (level instanceof ServerWorld) {
            ShinsuAttribute attribute = getAttribute();
            if (attribute != null) {
                Vector3d motion = getDeltaMovement();
                attribute.applyBlockEffect(this, latestHit == null || hasImpulse ? new BlockRayTraceResult(motion, Direction.getNearest(motion.x, motion.y, motion.z), blockPosition(), true) : latestHit);
            }
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            technique = nbt.getUUID("Technique");
        }
        if (nbt.contains("Attribute", Constants.NBT.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            nbt.putString("Attribute", attribute.getRegistryName().toString());
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
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
    private UUID technique;

    public ShinsuEntity(EntityType<ShinsuEntity> type, World world) {
        super(type, world);
        technique = null;
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
        return ShinsuAttribute.getParticles(null);
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
            target.hurt(ShinsuAttribute.getDamageSource(null), damage);
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
        super.onHitBlock(rayTraceResult);
        remove();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            technique = nbt.getUUID("Technique");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

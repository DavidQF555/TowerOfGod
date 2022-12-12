package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuEntity extends AbstractHurtingProjectile {

    private static final int PARTICLES = 3;
    private static final float DAMAGE = 0.625f;
    private UUID technique;

    public ShinsuEntity(EntityType<ShinsuEntity> type, Level world) {
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
    public ParticleOptions getTrailParticle() {
        return ShinsuAttribute.getParticles(null);
    }

    @Override
    public void setDeltaMovement(Vec3 motionIn) {
        super.setDeltaMovement(motionIn);
        double length = motionIn.length();
        xPower = motionIn.x() / length * 0.1;
        yPower = motionIn.y() / length * 0.1;
        zPower = motionIn.z() / length * 0.1;
    }

    @Override
    public void onHitEntity(EntityHitResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        if (level instanceof ServerLevel) {
            Entity shooter = getOwner();
            Entity target = rayTraceResult.getEntity();
            float damage = DAMAGE;
            if (shooter != null) {
                damage *= ShinsuStats.getNetResistance(shooter, target);
            }
            target.hurt(ShinsuAttribute.getDamageSource(null), damage);
        }
        remove(RemovalReason.DISCARDED);
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
            remove(RemovalReason.DISCARDED);
        }
        for (int i = 0; i < PARTICLES; i++) {
            level.addParticle(getTrailParticle(), getRandomX(1), getRandomY(), getRandomZ(1), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);
        remove(RemovalReason.DISCARDED);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Technique", Tag.TAG_INT_ARRAY)) {
            technique = nbt.getUUID("Technique");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
    }

}

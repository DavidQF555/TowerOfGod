package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.UUID;

public class ShinsuSpearEntity extends SpearEntity {

    private static final int PARTICLES = 2;
    private static final EntityDataAccessor<String> ATTRIBUTE = SynchedEntityData.defineId(ShinsuSpearEntity.class, EntityDataSerializers.STRING);
    private UUID technique;

    public ShinsuSpearEntity(EntityType<ShinsuSpearEntity> type, Level world) {
        super(type, world);
        setStack(ItemRegistry.SHINSU_SPEAR.get().getDefaultInstance());
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ATTRIBUTE, "");
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(getEntityData().get(ATTRIBUTE)));
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        getEntityData().set(ATTRIBUTE, attribute == null ? "" : ShinsuAttributeRegistry.getRegistry().getKey(attribute).toString());
    }

    @Override
    public void tick() {
        if (technique != null && getTechnique() == null) {
            remove(RemovalReason.DISCARDED);
        }
        ParticleOptions particle = ShinsuAttribute.getParticles(getAttribute());
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
    protected void tickDespawn() {
        if (inGroundTime >= 0) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        pickup = Pickup.DISALLOWED;
    }

    @Nullable
    public ShinsuTechniqueInstance getTechnique() {
        Entity shooter = getOwner();
        if (technique != null && shooter != null) {
            return ShinsuTechniqueInstance.get(shooter, technique);
        }
        return null;
    }

    public void setTechnique(UUID technique) {
        this.technique = technique;
    }

    @Override
    protected void onHitEntity(EntityHitResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            attribute.applyEntityEffect(this, rayTraceResult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            attribute.applyBlockEffect(this, rayTraceResult);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
        if (nbt.contains("Technique", Tag.TAG_INT_ARRAY)) {
            setTechnique(nbt.getUUID("Technique"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            nbt.putString("Attribute", ShinsuAttributeRegistry.getRegistry().getKey(attribute).toString());
        }
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
    }

}

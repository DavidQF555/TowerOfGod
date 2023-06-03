package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class ShinsuSpearEntity extends SpearEntity {

    private static final int PARTICLES = 2;
    private static final DataParameter<String> ATTRIBUTE = EntityDataManager.defineId(ShinsuSpearEntity.class, DataSerializers.STRING);
    private UUID technique;

    public ShinsuSpearEntity(EntityType<ShinsuSpearEntity> type, World world) {
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
        getEntityData().set(ATTRIBUTE, attribute == null ? "" : attribute.getRegistryName().toString());
    }

    @Override
    public void tick() {
        if (technique != null && getTechnique() == null) {
            remove();
        }
        IParticleData particle = ShinsuAttribute.getParticles(getAttribute());
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
            remove();
        }
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
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

    public void setTechnique(UUID technique) {
        this.technique = technique;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            attribute.applyEntityEffect(this, rayTraceResult);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            attribute.applyBlockEffect(this, rayTraceResult);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Attribute", Constants.NBT.TAG_STRING)) {
            setAttribute(ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute"))));
        }
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            setTechnique(nbt.getUUID("Technique"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            nbt.putString("Attribute", attribute.getRegistryName().toString());
        }
        if (technique != null) {
            nbt.putUUID("Technique", technique);
        }
    }

}

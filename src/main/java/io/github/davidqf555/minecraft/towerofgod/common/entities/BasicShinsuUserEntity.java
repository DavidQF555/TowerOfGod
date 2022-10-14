package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.client.model.CastingModelHelper;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public abstract class BasicShinsuUserEntity extends PathfinderMob implements IShinsuUser, IGeared<BasicShinsuUserEntity>, RangedAttackMob {

    private static final EntityDataAccessor<String> GROUP = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> ATTRIBUTE_PARTICLES = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.PARTICLE);

    public BasicShinsuUserEntity(EntityType<? extends BasicShinsuUserEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (dataTag == null) {
            initializeShinsuLevel(random);
        } else if (dataTag.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag child = dataTag.getCompound(TowerOfGod.MOD_ID);
            if (child.contains("Level", Tag.TAG_INT)) {
                ShinsuStats stats = getShinsuStats();
                stats.addLevel(child.getInt("Level") - stats.getLevel());
            } else {
                initializeShinsuLevel(random);
            }
        } else {
            initializeShinsuLevel(random);
        }
        initializeShinsuStats(worldIn);
        initializeWeapons();
        Group group = getGroup();
        MutableComponent text;
        if (group != null) {
            text = Component.translatable(getType().getDescriptionId() + ".group_name", group.getName(), getShinsuStats().getLevel()).withStyle(group.getTextFormattingColor());
        } else {
            text = Component.translatable(getType().getDescriptionId() + ".name", getShinsuStats().getLevel());
        }
        setCustomName(text);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(GROUP, "");
        getEntityData().define(CASTING, false);
        getEntityData().define(ATTRIBUTE_PARTICLES, ShinsuAttribute.getParticles(null));
    }

    public ParticleOptions getAttributeParticles() {
        return getEntityData().get(ATTRIBUTE_PARTICLES);
    }

    public void setAttributeParticles(ParticleOptions particles) {
        getEntityData().set(ATTRIBUTE_PARTICLES, particles);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        heal(0.025f);
        if (level.isClientSide() && isCasting()) {
            CastingModelHelper.spawnParticles(this, getAttributeParticles());
        }
    }

    @Override
    protected void customServerAiStep() {
        setAttributeParticles(ShinsuAttribute.getParticles(getShinsuStats().getAttribute()));
        shinsuTick((ServerLevel) level);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Group", Tag.TAG_STRING)) {
            setGroup(GroupRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Group"))));
        }
        if (nbt.contains("Casting", Tag.TAG_BYTE)) {
            setCasting(nbt.getBoolean("Casting"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        Group group = getGroup();
        if (group != null) {
            nbt.putString("Group", group.getId().toString());
        }
        nbt.putBoolean("Casting", isCasting());
    }

    @Override
    public ShinsuStats getShinsuStats() {
        return ShinsuStats.get(this);
    }

    @Nullable
    @Override
    public Group getGroup() {
        return GroupRegistry.getRegistry().getValue(new ResourceLocation(entityData.get(GROUP)));
    }

    @Override
    public void setGroup(@Nullable Group group) {
        entityData.set(GROUP, group == null ? "" : group.getId().toString());
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack hand = getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack ammo = getProjectile(hand);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, ammo, distanceFactor);
        if (getMainHandItem().getItem() instanceof BowItem) {
            arrow = ((BowItem) getMainHandItem().getItem()).customArrow(arrow);
        }
        double dX = target.getX() - getX();
        double dZ = target.getZ() - getZ();
        double dY = target.getY(0.3333333333333333) - arrow.getY() + Mth.sqrt((float) (dX * dX + dZ * dZ)) * 0.2;
        ShinsuStats stats = getShinsuStats();
        float velocity = 1.6f;
        float inaccuracy = (14 - level.getDifficulty().getId() * 4f) / stats.getLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vec3 dir = new Vec3(dX, dY, dZ);
            Optional<? extends ShinsuTechniqueInstance> technique = ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get().create(this, target, dir).left();
            if (technique.isPresent()) {
                ShootShinsuArrow inst = (ShootShinsuArrow) technique.get();
                inst.setVelocity(velocity);
                inst.getTechnique().cast(target, inst);
                return;
            } else {
                arrow = ((ArrowItem) Items.ARROW).createArrow(level, new ItemStack(Items.ARROW), this);
            }
        }
        arrow.shoot(dX, dY, dZ, velocity, inaccuracy);
        level.addFreshEntity(arrow);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getGearLevel() {
        return getShinsuStats().getLevel();
    }

    @Override
    public BasicShinsuUserEntity getGearedEntity() {
        return this;
    }

    @Override
    public boolean isWeaponPreferred(Item weapon) {
        Group group = getGroup();
        return group != null && group.isPreferredWeapon(weapon);
    }

    @Override
    public boolean isCasting() {
        return getEntityData().get(CASTING);
    }

    @Override
    public void setCasting(boolean casting) {
        getEntityData().set(CASTING, casting);
    }

}

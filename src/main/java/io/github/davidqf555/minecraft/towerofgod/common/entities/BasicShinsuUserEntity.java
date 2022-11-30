package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.client.model.CastingModelHelper;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class BasicShinsuUserEntity extends PathfinderMob implements IShinsuUser, IGeared<BasicShinsuUserEntity>, RangedAttackMob {

    private static final EntityDataAccessor<String> GROUP = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> ATTRIBUTE_PARTICLES = SynchedEntityData.defineId(BasicShinsuUserEntity.class, EntityDataSerializers.PARTICLE);
    private int shinsuLevel = 1;

    public BasicShinsuUserEntity(EntityType<? extends BasicShinsuUserEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (dataTag == null) {
            shinsuLevel = getInitialLevel(random);
        } else if (dataTag.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag child = dataTag.getCompound(TowerOfGod.MOD_ID);
            if (child.contains("Level", Tag.TAG_INT)) {
                shinsuLevel = child.getInt("Level");
            } else {
                shinsuLevel = getInitialLevel(random);
            }
        } else {
            shinsuLevel = getInitialLevel(random);
        }
        initialize(worldIn);
        initializeWeapons();
        Group group = getGroup();
        MutableComponent text;
        if (group != null) {
            text = new TranslatableComponent(getType().getDescriptionId() + ".group_name", group.getName(), getShinsuLevel()).withStyle(group.getTextFormattingColor());
        } else {
            text = new TranslatableComponent(getType().getDescriptionId() + ".name", getShinsuLevel());
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
    public void die(DamageSource source) {
        if (!level.isClientSide()) {
            LivingEntity credit = getKillCredit();
            if (credit != null) {
                ShinsuStats killed = getShinsuStats();
                ShinsuStats stats = ShinsuStats.get(credit);
                stats.setMaxShinsu(stats.getMaxShinsu() + 1 + Math.max(0, killed.getMaxShinsu() - stats.getMaxShinsu()) / 10);
                stats.setTension(stats.getTension() * (1 + Math.max(0, killed.getTension() - stats.getTension()) / 5));
                stats.setResistance(stats.getResistance() * (1 + Math.max(0, killed.getResistance() - stats.getResistance()) / 5));
                if (credit instanceof ServerPlayer) {
                    TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) credit), new UpdateShinsuMeterPacket(ShinsuStats.getShinsu(credit), stats.getMaxShinsu()));
                }
            }
        }
        super.die(source);
    }

    @Override
    public int getShinsuLevel() {
        return shinsuLevel;
    }

    protected int getInitialLevel(Random rand) {
        int min = getMinInitialLevel();
        int total = getMaxInitialLevel() - min;
        double current = 0;
        double random = rand.nextDouble();
        double rate = 0.8;
        double choose = 1;
        double success = 1;
        double fail = Math.pow(1 - rate, total - 1);
        for (int i = 0; i < total; i++) {
            double chance = choose * success * fail;
            current += chance;
            if (random < current) {
                return i + min;
            }
            choose *= (total - i - 1.0) / (i + 1);
            success *= rate;
            fail /= 1 - rate;
        }
        return total;
    }

    protected abstract int getMinInitialLevel();

    protected abstract int getMaxInitialLevel();

    @Override
    protected void customServerAiStep() {
        setAttributeParticles(ShinsuAttribute.getParticles(getShinsuQualityData().getAttribute()));
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
        if (nbt.contains("Level", Tag.TAG_INT)) {
            shinsuLevel = nbt.getInt("Level");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        Group group = getGroup();
        if (group != null) {
            nbt.putString("Group", group.getRegistryName().toString());
        }
        nbt.putBoolean("Casting", isCasting());
        nbt.putInt("Level", shinsuLevel);
    }

    @Override
    public ShinsuStats getShinsuStats() {
        return ShinsuStats.get(this);
    }

    @Override
    public ShinsuQualityData getShinsuQualityData() {
        return ShinsuQualityData.get(this);
    }

    @Override
    public ShinsuTechniqueData<BasicShinsuUserEntity> getShinsuTechniqueData() {
        return ShinsuTechniqueData.get(this);
    }

    @Nullable
    @Override
    public Group getGroup() {
        return GroupRegistry.getRegistry().getValue(new ResourceLocation(entityData.get(GROUP)));
    }

    @Override
    public void setGroup(@Nullable Group group) {
        entityData.set(GROUP, group == null ? "" : group.getRegistryName().toString());
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
        float velocity = 1.6f;
        float inaccuracy = (14 - level.getDifficulty().getId() * 4f) / getShinsuLevel();
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
        return getShinsuLevel();
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

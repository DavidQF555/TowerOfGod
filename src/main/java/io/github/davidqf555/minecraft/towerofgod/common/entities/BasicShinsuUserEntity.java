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
import net.minecraft.entity.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class BasicShinsuUserEntity extends CreatureEntity implements IShinsuUser, IGeared<BasicShinsuUserEntity>, IRangedAttackMob {

    private static final DataParameter<String> GROUP = EntityDataManager.defineId(BasicShinsuUserEntity.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> CASTING = EntityDataManager.defineId(BasicShinsuUserEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<IParticleData> ATTRIBUTE_PARTICLES = EntityDataManager.defineId(BasicShinsuUserEntity.class, DataSerializers.PARTICLE);
    private int shinsuLevel = 1;

    public BasicShinsuUserEntity(EntityType<? extends BasicShinsuUserEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (dataTag == null) {
            shinsuLevel = getInitialLevel(random);
        } else if (dataTag.contains(TowerOfGod.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT child = dataTag.getCompound(TowerOfGod.MOD_ID);
            if (child.contains("Level", Constants.NBT.TAG_INT)) {
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
        IFormattableTextComponent text;
        if (group != null) {
            text = new TranslationTextComponent(getType().getDescriptionId() + ".group_name", group.getName(), getLevel()).withStyle(group.getTextFormattingColor());
        } else {
            text = new TranslationTextComponent(getType().getDescriptionId() + ".name", getLevel());
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

    public IParticleData getAttributeParticles() {
        return getEntityData().get(ATTRIBUTE_PARTICLES);
    }

    public void setAttributeParticles(IParticleData particles) {
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
                if (credit instanceof ServerPlayerEntity) {
                    TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) credit), new UpdateShinsuMeterPacket(ShinsuStats.getShinsu(credit), stats.getMaxShinsu()));
                }
            }
        }
        super.die(source);
    }

    @Override
    public int getLevel() {
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
        shinsuTick((ServerWorld) level);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Group", Constants.NBT.TAG_STRING)) {
            setGroup(GroupRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Group"))));
        }
        if (nbt.contains("Casting", Constants.NBT.TAG_BYTE)) {
            setCasting(nbt.getBoolean("Casting"));
        }
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            shinsuLevel = nbt.getInt("Level");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
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
        ItemStack hand = getItemInHand(Hand.MAIN_HAND);
        ItemStack ammo = getProjectile(hand);
        AbstractArrowEntity arrow = ProjectileHelper.getMobArrow(this, ammo, distanceFactor);
        if (getMainHandItem().getItem() instanceof BowItem) {
            arrow = ((BowItem) getMainHandItem().getItem()).customArrow(arrow);
        }
        double dX = target.getX() - getX();
        double dZ = target.getZ() - getZ();
        double dY = target.getY(0.3333333333333333) - arrow.getY() + MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2;
        float velocity = 1.6f;
        float inaccuracy = (14 - level.getDifficulty().getId() * 4f) / getLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vector3d dir = new Vector3d(dX, dY, dZ);
            Optional<? extends ShinsuTechniqueInstance> technique = ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get().create(this, target, dir).left();
            if (technique.isPresent()) {
                ShootShinsuArrow inst = (ShootShinsuArrow) technique.get();
                inst.setVelocity(velocity * 3);
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
        return getLevel();
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

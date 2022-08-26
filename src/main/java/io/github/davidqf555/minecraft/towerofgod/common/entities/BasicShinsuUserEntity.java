package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public abstract class BasicShinsuUserEntity extends CreatureEntity implements IShinsuUser, IGeared<BasicShinsuUserEntity>, IRangedAttackMob {

    private static final DataParameter<String> GROUP = EntityDataManager.defineId(BasicShinsuUserEntity.class, DataSerializers.STRING);

    public BasicShinsuUserEntity(EntityType<? extends BasicShinsuUserEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        initializeShinsuStats(worldIn);
        initializeWeapons();
        Group group = getGroup();
        IFormattableTextComponent text;
        if (group != null) {
            text = new TranslationTextComponent(getType().getDescriptionId() + ".group_name", group.getName(), ShinsuStats.get(this).getLevel()).withStyle(group.getTextFormattingColor());
        } else {
            text = new TranslationTextComponent(getType().getDescriptionId() + ".name", ShinsuStats.get(this).getLevel());
        }
        setCustomName(text);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(GROUP, "");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        heal(0.025f);
    }

    @Override
    protected void customServerAiStep() {
        shinsuTick((ServerWorld) level);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Group", Constants.NBT.TAG_STRING)) {
            setGroup(GroupRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Group"))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        Group group = getGroup();
        if (group != null) {
            nbt.putString("Group", group.getRegistryName().toString());
        }
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
        ShinsuStats stats = ShinsuStats.get(this);
        float velocity = 1.6f;
        float inaccuracy = (14 - level.getDifficulty().getId() * 4f) / stats.getLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vector3d dir = new Vector3d(dX, dY, dZ);
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
        return ShinsuStats.get(this).getLevel();
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


}

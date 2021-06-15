package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.goals.RangedMainHandAttackGoal;
import io.github.davidqf555.minecraft.towerofgod.common.entities.goals.SwapWeaponToMainHandGoal;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShootShinsuArrow;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class RegularEntity extends CreatureEntity implements IShinsuUser<RegularEntity>, IGeared<RegularEntity>, IRangedAttackMob {

    public static final DataParameter<String> GROUP = EntityDataManager.createKey(RegularEntity.class, DataSerializers.STRING);
    private static final String NAME = "entity." + TowerOfGod.MOD_ID + ".regular_entity.name";
    private int level;
    private Personality personality;

    public RegularEntity(World worldIn) {
        super(RegistryHandler.REGULAR_ENTITY.get(), worldIn);
        personality = Personality.NEUTRAL;
        this.level = 1;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RegularEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.215)
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        initializeShinsuStats(worldIn);
        initializeWeapons();
        personality = MonsterEntity.isValidLightLevel(worldIn, getPosition().down(), rand) && rand.nextBoolean() ? Personality.AGGRESSIVE : Personality.NEUTRAL;
        setCustomName(new TranslationTextComponent(NAME, getShinsuLevel()).mergeStyle(getGroup().getTextFormattingColor()));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(GROUP, Group.NONE.name());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        heal(0.025f);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new FindRegularTeamGoal(this, 5, 16));
        goalSelector.addGoal(2, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(3, new CastShinsuGoal<>(this));
        goalSelector.addGoal(4, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(7, new FollowRegularTeamLeaderGoal(this, 1, 8));
        goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(9, new LookRandomlyGoal(this));
        targetSelector.addGoal(0, new RegularHurtByTargetGoal(this));
        targetSelector.addGoal(1, new RegularNearestAttackableTargetGoal(this));
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Personality", Constants.NBT.TAG_STRING)) {
            personality = Personality.valueOf(nbt.getString("Personality"));
        }
        if (nbt.contains("Group", Constants.NBT.TAG_STRING)) {
            setGroup(Group.valueOf(nbt.getString("Group")));
        }
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            setShinsuLevel(nbt.getInt("Level"));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putString("Personality", personality.name());
        nbt.putString("Group", getGroup().name());
        nbt.putInt("Level", getShinsuLevel());
    }

    @Override
    public RegularEntity getShinsuUserEntity() {
        return this;
    }

    @Override
    public int getMaxInitialLevel(int floor) {
        return floor * 10;
    }

    @Override
    public int getMinInitialLevel(int floor) {
        return floor * 3;
    }

    @Override
    public int getShinsuLevel() {
        return level;
    }

    @Override
    public void setShinsuLevel(int level) {
        this.level = level;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return level - rand.nextInt(3) + 7;
    }

    @Override
    public Group getGroup() {
        return Group.valueOf(dataManager.get(GROUP));
    }

    @Override
    public void setGroup(Group group) {
        dataManager.set(GROUP, group.name());
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack hand = getHeldItem(Hand.MAIN_HAND);
        ItemStack ammo = findAmmo(hand);
        AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, ammo, distanceFactor);
        if (getHeldItemMainhand().getItem() instanceof BowItem) {
            arrow = ((BowItem) getHeldItemMainhand().getItem()).customArrow(arrow);
        }
        double dX = target.getPosX() - getPosX();
        double dZ = target.getPosZ() - getPosZ();
        double dY = target.getPosYHeight(0.3333333333333333) - arrow.getPosY() + MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2;
        float velocity = 1.6f;
        float inaccuracy = (14 - world.getDifficulty().getId() * 4f) / getShinsuLevel();
        if (arrow instanceof ShinsuArrowEntity) {
            Vector3d dir = new Vector3d(dX, dY, dZ);
            IShinsuStats stats = IShinsuStats.get(this);
            ShinsuTechniqueInstance technique = ShinsuTechnique.SHOOT_SHINSU_ARROW.getBuilder().doBuild(this, ShootShinsuArrow.getLevelForVelocity(velocity, stats.getQuality()), target, dir, null);
            if (technique == null) {
                arrow = ((ArrowItem) Items.ARROW).createArrow(world, new ItemStack(Items.ARROW), this);
            } else {
                stats.cast((ServerWorld) world, technique);
                return;
            }
        }
        arrow.shoot(dX, dY, dZ, velocity, inaccuracy);
        world.addEntity(arrow);
    }

    @Nullable
    public RegularTeamsSavedData.RegularTeam getRegularTeam() {
        if (world instanceof ServerWorld) {
            return RegularTeamsSavedData.getOrCreateTeam((ServerWorld) world, this);
        }
        return null;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        RegularTeamsSavedData.RegularTeam team = getRegularTeam();
        return super.isOnSameTeam(entityIn) || (team != null && team.getMembers().contains(entityIn.getUniqueID()));
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getGearLevel() {
        return getShinsuLevel();
    }

    @Override
    public RegularEntity getGearedEntity() {
        return this;
    }

    @Override
    public boolean isWeaponPreferred(Item weapon) {
        return getGroup().isPreferredWeapon(weapon);
    }

    private enum Personality {

        AGGRESSIVE(),
        NEUTRAL();

    }

    public static class Factory implements EntityType.IFactory<RegularEntity> {
        @Nonnull
        @Override
        public RegularEntity create(@Nullable EntityType<RegularEntity> type, World world) {
            return new RegularEntity(world);
        }
    }

    private static class FollowRegularTeamLeaderGoal extends Goal {

        private final RegularEntity entity;
        private final float speed;
        private final double range;
        private Entity leader;

        public FollowRegularTeamLeaderGoal(RegularEntity entity, float speed, double range) {
            this.entity = entity;
            this.speed = speed;
            this.range = range;
            leader = null;
            setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            if (!(entity.world instanceof ServerWorld) || team == null || entity.getAttackTarget() != null) {
                return false;
            } else {
                UUID id = team.getLeader();
                if (id == null || entity.getUniqueID().equals(id)) {
                    return false;
                } else {
                    leader = ((ServerWorld) entity.world).getEntityByUuid(id);
                    return leader != null && entity.getDistanceSq(leader) > range * range;
                }
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !entity.getNavigator().noPath() && entity.getAttackTarget() == null && entity.getDistanceSq(leader) > range * range;
        }

        @Override
        public void tick() {
            entity.getNavigator().tryMoveToEntityLiving(leader, speed);
        }

        @Override
        public void resetTask() {
            entity.getNavigator().clearPath();
            leader = null;
        }
    }

    private static class RegularNearestAttackableTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

        public RegularNearestAttackableTargetGoal(RegularEntity entity) {
            super(entity, LivingEntity.class, 10, true, false, target -> (target instanceof RegularEntity || target instanceof PlayerEntity) && !entity.isOnSameTeam(target));
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && ((RegularEntity) goalOwner).personality == Personality.AGGRESSIVE;
        }

    }

    private static class FindRegularTeamGoal extends Goal {

        private final RegularEntity entity;
        private final int maxSize;
        private final double range;

        public FindRegularTeamGoal(RegularEntity entity, int maxSize, double range) {
            this.entity = entity;
            this.maxSize = maxSize;
            this.range = range;
        }

        @Override
        public boolean shouldExecute() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            return team != null && team.getMembers().size() < maxSize;
        }

        @Override
        public void startExecuting() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            if (team != null) {
                List<UUID> members = team.getMembers();
                AxisAlignedBB bounds = AxisAlignedBB.fromVector(entity.getPositionVec()).grow(range);
                List<RegularEntity> nearby = entity.world.getEntitiesWithinAABB(RegularEntity.class, bounds, EntityPredicates.NOT_SPECTATING.and(reg -> reg.getDistanceSq(entity) <= range * range));
                UUID id = entity.getUniqueID();
                for (RegularEntity near : nearby) {
                    if (near.personality == entity.personality) {
                        RegularTeamsSavedData.RegularTeam nearTeam = near.getRegularTeam();
                        if (nearTeam != null) {
                            List<UUID> nearMembers = nearTeam.getMembers();
                            if (!nearMembers.contains(id) && members.size() + nearMembers.size() <= maxSize) {
                                nearMembers.addAll(members);
                                members.clear();
                                return;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return false;
        }
    }

    private static class RegularHurtByTargetGoal extends HurtByTargetGoal {

        public RegularHurtByTargetGoal(RegularEntity entity) {
            super(entity);
            setCallsForHelp(RegularEntity.class);
        }

        @Override
        protected boolean isSuitableTarget(@Nullable LivingEntity potentialTarget, EntityPredicate targetPredicate) {
            return super.isSuitableTarget(potentialTarget, targetPredicate) && !goalOwner.isOnSameTeam(potentialTarget);
        }

        @Override
        protected void alertOthers() {
            LivingEntity revenge = goalOwner.getRevengeTarget();
            if (revenge != null) {
                double range = this.getTargetDistance();
                AxisAlignedBB bounds = AxisAlignedBB.fromVector(goalOwner.getPositionVec()).grow(range, 10, range);
                List<RegularEntity> nearby = goalOwner.world.getLoadedEntitiesWithinAABB(RegularEntity.class, bounds);
                for (RegularEntity near : nearby) {
                    if (!goalOwner.equals(near) && near.getAttackTarget() == null && near.isOnSameTeam(goalOwner) && !near.isOnSameTeam(revenge)) {
                        near.setAttackTarget(revenge);
                    }
                }
            }
        }

    }
}

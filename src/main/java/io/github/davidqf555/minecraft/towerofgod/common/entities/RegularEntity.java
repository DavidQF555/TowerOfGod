package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class RegularEntity extends BasicShinsuUserEntity {

    private Personality personality;

    public RegularEntity(EntityType<RegularEntity> type, Level world) {
        super(type, world);
        personality = Personality.NEUTRAL;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return RegularEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        personality = Monster.isDarkEnoughToSpawn(worldIn, blockPosition().below(), random) && random.nextBoolean() ? RegularEntity.Personality.AGGRESSIVE : RegularEntity.Personality.NEUTRAL;
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new FindRegularTeamGoal(this, 5, 16));
        goalSelector.addGoal(2, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(3, new CastShinsuGoal<>(this));
        goalSelector.addGoal(4, new RangedMainHandAttackGoal<>(this, 1, 12, 15));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.5, true));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(7, new FollowRegularTeamLeaderGoal(this, 1, 8));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new RegularHurtByTargetGoal(this));
        targetSelector.addGoal(1, new RegularNearestAttackableTargetGoal(this));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Personality", Tag.TAG_INT)) {
            personality = Personality.values()[nbt.getInt("Personality")];
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Personality", personality.ordinal());
    }

    @Override
    public int getMinInitialLevel() {
        return 1;
    }

    @Override
    public int getMaxInitialLevel() {
        return 50;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return ShinsuStats.get(this).getLevel() - random.nextInt(3) + 7;
    }

    @Nullable
    public RegularTeamsSavedData.RegularTeam getRegularTeam() {
        if (level instanceof ServerLevel) {
            return RegularTeamsSavedData.getOrCreateTeam((ServerLevel) level, this);
        }
        return null;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        RegularTeamsSavedData.RegularTeam team = getRegularTeam();
        return super.isAlliedTo(entityIn) || (team != null && team.getMembers().contains(entityIn.getUUID()));
    }

    private enum Personality {

        AGGRESSIVE(),
        NEUTRAL()

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
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            if (!(entity.level instanceof ServerLevel) || team == null || entity.getTarget() != null) {
                return false;
            } else {
                UUID id = team.getLeader();
                if (id == null || entity.getUUID().equals(id)) {
                    return false;
                } else {
                    leader = ((ServerLevel) entity.level).getEntity(id);
                    return leader != null && entity.distanceToSqr(leader) > range * range;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !entity.getNavigation().isDone() && entity.getTarget() == null && entity.distanceToSqr(leader) > range * range;
        }

        @Override
        public void tick() {
            entity.getNavigation().moveTo(leader, speed);
        }

        @Override
        public void stop() {
            entity.getNavigation().stop();
            leader = null;
        }
    }

    private static class RegularNearestAttackableTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

        public RegularNearestAttackableTargetGoal(RegularEntity entity) {
            super(entity, LivingEntity.class, 10, true, false, target -> (target instanceof RegularEntity || target instanceof Player) && !entity.isAlliedTo(target));
        }

        @Override
        public boolean canUse() {
            return super.canUse() && ((RegularEntity) mob).personality == Personality.AGGRESSIVE;
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
        public boolean canUse() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            return team != null && team.getMembers().size() < maxSize;
        }

        @Override
        public void start() {
            RegularTeamsSavedData.RegularTeam team = entity.getRegularTeam();
            if (team != null) {
                List<UUID> members = team.getMembers();
                AABB bounds = AABB.ofSize(entity.position(), range, range, range);
                List<RegularEntity> nearby = entity.level.getEntitiesOfClass(RegularEntity.class, bounds, EntitySelector.NO_SPECTATORS.and(reg -> reg.distanceToSqr(entity) <= range * range));
                UUID id = entity.getUUID();
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
        public boolean canContinueToUse() {
            return false;
        }
    }

    private static class RegularHurtByTargetGoal extends HurtByTargetGoal {

        public RegularHurtByTargetGoal(RegularEntity entity) {
            super(entity);
            setAlertOthers(RegularEntity.class);
        }

        @Override
        protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
            return super.canAttack(potentialTarget, targetPredicate) && !mob.isAlliedTo(potentialTarget);
        }

        @Override
        protected void alertOthers() {
            LivingEntity revenge = mob.getLastHurtByMob();
            if (revenge != null) {
                double range = this.getFollowDistance();
                AABB bounds = AABB.ofSize(mob.position(), range, 10, range);
                List<RegularEntity> nearby = mob.level.getEntitiesOfClass(RegularEntity.class, bounds);
                for (RegularEntity near : nearby) {
                    if (!mob.equals(near) && near.getTarget() == null && near.isAlliedTo(mob) && !near.isAlliedTo(revenge)) {
                        near.setTarget(revenge);
                    }
                }
            }
        }

    }
}

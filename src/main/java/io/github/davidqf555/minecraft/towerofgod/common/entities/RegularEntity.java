package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class RegularEntity extends BasicShinsuUserEntity {

    private Personality personality;

    public RegularEntity(EntityType<RegularEntity> type, World world) {
        super(type, world);
        personality = Personality.NEUTRAL;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return RegularEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        personality = MonsterEntity.isDarkEnoughToSpawn(worldIn, blockPosition().below(), random) && random.nextBoolean() ? RegularEntity.Personality.AGGRESSIVE : RegularEntity.Personality.NEUTRAL;
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new FindRegularTeamGoal(this, 5, 16));
        goalSelector.addGoal(2, new SwapWeaponToMainHandGoal<>(this, 5));
        goalSelector.addGoal(3, new CastShinsuGoal<>(this, 100));
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
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Personality", Constants.NBT.TAG_INT)) {
            personality = Personality.values()[nbt.getInt("Personality")];
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Personality", personality.ordinal());
    }

    @Override
    public int getMinInitialLevel() {
        return 1;
    }

    @Override
    public int getMaxInitialLevel() {
        return 75;
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return getLevel() - random.nextInt(3) + 7;
    }

    @Nullable
    public RegularTeamsSavedData.RegularTeam getRegularTeam() {
        if (level instanceof ServerWorld) {
            return RegularTeamsSavedData.getOrCreateTeam((ServerWorld) level, this);
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
            if (!(entity.level instanceof ServerWorld) || team == null || entity.getTarget() != null) {
                return false;
            } else {
                UUID id = team.getLeader();
                if (id == null || entity.getUUID().equals(id)) {
                    return false;
                } else {
                    leader = ((ServerWorld) entity.level).getEntity(id);
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
            super(entity, LivingEntity.class, 10, true, false, target -> (target instanceof RegularEntity || target instanceof PlayerEntity) && !entity.isAlliedTo(target));
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
                AxisAlignedBB bounds = AxisAlignedBB.ofSize(range, range, range).move(entity.position());
                List<RegularEntity> nearby = entity.level.getEntitiesOfClass(RegularEntity.class, bounds, EntityPredicates.NO_SPECTATORS.and(reg -> reg.distanceToSqr(entity) <= range * range));
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
        protected boolean canAttack(@Nullable LivingEntity potentialTarget, EntityPredicate targetPredicate) {
            return super.canAttack(potentialTarget, targetPredicate) && !mob.isAlliedTo(potentialTarget);
        }

        @Override
        protected void alertOthers() {
            LivingEntity revenge = mob.getLastHurtByMob();
            if (revenge != null) {
                double range = this.getFollowDistance();
                AxisAlignedBB bounds = AxisAlignedBB.ofSize(range, 10, range).move(mob.position());
                List<RegularEntity> nearby = mob.level.getLoadedEntitiesOfClass(RegularEntity.class, bounds);
                for (RegularEntity near : nearby) {
                    if (!mob.equals(near) && near.getTarget() == null && near.isAlliedTo(mob) && !near.isAlliedTo(revenge)) {
                        near.setTarget(revenge);
                    }
                }
            }
        }

    }
}

package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.CastingHelper;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class BaangEntity extends PathfinderMob {

    private ConfiguredShinsuTechniqueType<?, ?> type;
    private UUID user;
    private UUID technique;

    public BaangEntity(EntityType<? extends BaangEntity> type, Level world) {
        super(type, world);
        moveControl = new FlyingMoveControl(this, 90, true);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FollowUser(1, 4));
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel) {
            LivingEntity user = getUser();
            if (user == null) {
                discard();
            } else {
                UUID technique = getTechniqueID();
                if (technique != null && ShinsuTechniqueInstance.getById(user, technique) == null) {
                    discard();
                }
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer && hand == InteractionHand.MAIN_HAND && !CastingHelper.isCasting(player)) {
            CastingHelper.startCasting((ServerPlayer) player, this);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemovedFromWorld() {
        LivingEntity user = getUser();
        if (user instanceof ServerPlayer && CastingHelper.isCasting((Player) user)) {
            CastingHelper.stopCasting((ServerPlayer) user);
        }
        super.onRemovedFromWorld();
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.isBypassInvul()) {
            return true;
        }
        if (source instanceof EntityDamageSource && level instanceof ServerLevel) {
            UUID id = getUserID();
            Entity entity = source.getDirectEntity();
            if (id != null && entity != null && id.equals(entity.getUUID())) {
                discard();
                return true;
            }
        }
        return false;
    }

    @Nullable
    public UUID getUserID() {
        return user;
    }

    @Nullable
    public UUID getTechniqueID() {
        return technique;
    }

    public void setTechniqueID(@Nullable UUID technique) {
        this.technique = technique;
    }

    public void setUserID(@Nullable UUID user) {
        this.user = user;
    }

    public ConfiguredShinsuTechniqueType<?, ?> getTechniqueType() {
        return type;
    }

    public void setTechniqueType(ConfiguredShinsuTechniqueType<?, ?> type) {
        this.type = type;
    }

    @Nullable
    public LivingEntity getUser() {
        UUID id = getUserID();
        if (id != null && level instanceof ServerLevel) {
            Entity user = ((ServerLevel) level).getEntity(id);
            if (user instanceof LivingEntity) {
                return (LivingEntity) user;
            }
        }
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("User", Tag.TAG_INT_ARRAY)) {
            setUserID(nbt.getUUID("User"));
        }
        if (nbt.contains("Technique", Tag.TAG_INT_ARRAY)) {
            setTechniqueID(nbt.getUUID("Technique"));
        }
        if (nbt.contains("TechniqueType", Tag.TAG_STRING)) {
            setTechniqueType(ConfiguredTechniqueTypeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("TechniqueType"))));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        if (getUserID() != null) {
            tag.putUUID("User", getUserID());
        }
        if (getTechniqueID() != null) {
            tag.putUUID("Technique", getTechniqueID());
        }
        tag.putString("TechniqueType", ConfiguredTechniqueTypeRegistry.getRegistry().getKey(getTechniqueType()).toString());
        return tag;
    }

    public class FollowUser extends Goal {

        private final double within, speed;
        private LivingEntity target;
        private Vec3 pos;

        public FollowUser(double speed, double within) {
            this.within = within;
            this.speed = speed;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            target = getUser();
            if (target != null) {
                this.pos = DefaultRandomPos.getPosTowards(BaangEntity.this, 16, 7, target.position(), Math.PI / 2);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (pos == null || !getNavigation().moveTo(pos.x(), pos.y(), pos.z(), speed)) {
                randomTeleport(target.getX(), target.getY(), target.getZ(), false);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !getNavigation().isDone() && target.isAlive() && target.distanceToSqr(BaangEntity.this) < within * within;
        }

        @Override
        public void stop() {
            target = null;
        }

    }

}

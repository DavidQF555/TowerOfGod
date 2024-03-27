package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.CastingHelper;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class BaangEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> KEY = SynchedEntityData.defineId(BaangEntity.class, EntityDataSerializers.STRING);
    private UUID userId;
    private UUID technique;
    private Entity user;
    private ConfiguredShinsuTechniqueType<?, ?> type;

    public BaangEntity(EntityType<? extends BaangEntity> type, Level world) {
        super(type, world);
        setNoGravity(true);
        moveControl = new FlyingMoveControl(this, 90, true);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.215)
                .add(Attributes.MAX_HEALTH, 1);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FollowUser(1, 4, 12));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(KEY, "");
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel) {
            LivingEntity user = getUser();
            UUID technique = getTechniqueID();
            if (user == null || getTechniqueTypeKey() == null) {
                discard();
            } else if (technique != null && ShinsuTechniqueInstance.getById(user, technique) == null) {
                discard();
            }
        }
    }

    @Override
    public float getBrightness() {
        return 1;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer && hand == InteractionHand.MAIN_HAND && !CastingHelper.isCasting(player)) {
            LivingEntity user = getUser();
            if (user != null && player.getUUID().equals(user.getUUID())) {
                CastingHelper.startCasting((ServerPlayer) player, this);
                return InteractionResult.CONSUME;
            }
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

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("User", Tag.TAG_INT_ARRAY)) {
            setUserID(nbt.getUUID("User"));
        }
        if (nbt.contains("Technique", Tag.TAG_INT_ARRAY)) {
            setTechniqueID(nbt.getUUID("Technique"));
        }
        if (nbt.contains("TechniqueType", Tag.TAG_STRING)) {
            setTechniqueType(ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, new ResourceLocation(nbt.getString("TechniqueType"))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getUserID() != null) {
            tag.putUUID("User", getUserID());
        }
        if (getTechniqueID() != null) {
            tag.putUUID("Technique", getTechniqueID());
        }
        if (getTechniqueTypeKey() != null) {
            tag.putString("TechniqueType", getTechniqueTypeKey().location().toString());
        }
    }

    @Nullable
    public UUID getUserID() {
        return userId;
    }

    @Nullable
    public UUID getTechniqueID() {
        return technique;
    }

    public void setTechniqueID(@Nullable UUID technique) {
        this.technique = technique;
    }

    public void setUserID(@Nullable UUID user) {
        this.userId = user;
        this.user = null;
    }

    @Nullable
    public ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> getTechniqueTypeKey() {
        String s = getEntityData().get(KEY);
        return s.isEmpty() ? null : ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, new ResourceLocation(s));
    }

    @Nullable
    public ConfiguredShinsuTechniqueType<?, ?> getTechniqueType() {
        if (getTechniqueTypeKey() != null && type == null) {
            Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(level.isClientSide() ? ClientReference.getRegistryAccess() : getServer().registryAccess());
            type = registry.getOrThrow(getTechniqueTypeKey());
        }
        return type;
    }

    public void setTechniqueType(ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> type) {
        getEntityData().set(KEY, type.location().toString());
        this.type = null;
    }

    @Nullable
    public LivingEntity getUser() {
        if (user == null) {
            UUID id = getUserID();
            if (id != null && level instanceof ServerLevel) {
                Entity entity = ((ServerLevel) level).getEntity(id);
                if (entity != null) {
                    user = entity;
                }
            }
        }
        return user instanceof LivingEntity && user.isAlive() ? (LivingEntity) user : null;
    }

    private class FollowUser extends Goal {

        private final double speed, stopDist, tpDist;
        private int recalcTime;

        private FollowUser(double speed, double stopDist, double tpDist) {
            this.speed = speed;
            this.stopDist = stopDist;
            this.tpDist = tpDist;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity user = getUser();
            return user != null && !position().closerThan(user.getEyePosition(), stopDist);
        }

        @Override
        public void start() {
            recalcTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity user = getUser();
            return user != null && !navigation.isDone() && !position().closerThan(user.getEyePosition(), stopDist);
        }

        @Override
        public void tick() {
            if (--recalcTime <= 0) {
                Vec3 pos = getUser().getEyePosition();
                recalcTime = adjustedTickDelay(10);
                if (!pos.closerThan(position(), tpDist) || !getNavigation().moveTo(pos.x(), pos.y(), pos.z(), speed)) {
                    double x = random.nextDouble(-3, 3);
                    double y = random.nextDouble(-1, 3);
                    double z = random.nextDouble(-3, 3);
                    teleportTo(pos.x() + x, pos.y() + y, pos.z() + z);
                    getNavigation().stop();
                }
            }
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }

    }

}

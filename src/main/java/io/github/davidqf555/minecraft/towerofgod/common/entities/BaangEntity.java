package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.CastingHelper;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class BaangEntity extends Entity {

    private static final double HOVER_RANGE = 10, SPEED = 0.05;
    private static final EntityDataAccessor<String> KEY = SynchedEntityData.defineId(BaangEntity.class, EntityDataSerializers.STRING);
    private UUID user;
    private UUID technique;
    private ConfiguredShinsuTechniqueType<?, ?> type;

    public BaangEntity(EntityType<? extends BaangEntity> type, Level world) {
        super(type, world);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(KEY, "");
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel) {
            LivingEntity user = getUser();
            if (user == null || getTechniqueTypeKey() == null) {
                discard();
            } else {
                UUID technique = getTechniqueID();
                if (technique != null && ShinsuTechniqueInstance.getById(user, technique) == null) {
                    discard();
                } else if (!user.getEyePosition().closerThan(position(), HOVER_RANGE)) {
                    Vec3 dir = user.getEyePosition().subtract(position()).normalize();
                    setPos(position().add(dir.scale(SPEED)));
                }
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer && hand == InteractionHand.MAIN_HAND && !CastingHelper.isCasting(player)) {
            CastingHelper.startCasting((ServerPlayer) player, this);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
    protected void readAdditionalSaveData(CompoundTag nbt) {
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
    protected void addAdditionalSaveData(CompoundTag tag) {
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
        UUID id = getUserID();
        if (id != null && level instanceof ServerLevel) {
            Entity user = ((ServerLevel) level).getEntity(id);
            if (user instanceof LivingEntity) {
                return (LivingEntity) user;
            }
        }
        return null;
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ObserverChangeHighlightPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ObserverEntity extends FlyingDevice {

    private static final double RANGE = 24;
    private final Set<UUID> targets;

    public ObserverEntity(World worldIn) {
        super(RegistryHandler.OBSERVER_ENTITY.get(), worldIn);
        targets = new HashSet<>();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.3)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3)
                .createMutableAttribute(Attributes.MAX_HEALTH, 10);
    }

    @Override
    public void livingTick() {
        targets.clear();
        AxisAlignedBB bounds = AxisAlignedBB.fromVector(getPositionVec()).grow(RANGE);
        for (Entity entity : world.getEntitiesInAABBexcluding(this, bounds, target -> EntityPredicates.CAN_AI_TARGET.test(target) && getDistanceSq(target) <= RANGE * RANGE && canEntityBeSeen(target))) {
            targets.add(entity.getUniqueID());
        }
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUniqueID(), targets));
        }
        super.livingTick();
    }

    @Override
    protected ItemStack getDeviceItem() {
        return RegistryHandler.OBSERVER_ITEM.get().getDefaultInstance();
    }

    @Override
    public void setOwnerID(@Nonnull UUID id) {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUniqueID(), new HashSet<>()));
        }
        super.setOwnerID(id);
    }

    @Override
    public void onRemovedFromWorld() {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUniqueID(), new HashSet<>()));
        }
        super.onRemovedFromWorld();
    }

    public static class Factory implements EntityType.IFactory<ObserverEntity> {
        @Nonnull
        @Override
        public ObserverEntity create(@Nullable EntityType<ObserverEntity> type, @Nonnull World world) {
            return new ObserverEntity(world);
        }
    }
}

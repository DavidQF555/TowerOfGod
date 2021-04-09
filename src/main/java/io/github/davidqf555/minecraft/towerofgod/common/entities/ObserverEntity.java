package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ObserverChangeHighlightMessage;
import io.github.davidqf555.minecraft.towerofgod.common.packets.RemoveObserverDataMessage;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class ObserverEntity extends FlyingDevice {

    private static final double RANGE = 24;
    private final ArrayList<UUID> targets;

    public ObserverEntity(World worldIn) {
        super(RegistryHandler.OBSERVER_ENTITY.get(), worldIn);
        targets = new ArrayList<>();
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
        Vector3d pos = getPositionVec();
        AxisAlignedBB bounds = new AxisAlignedBB(pos.subtract(RANGE, RANGE, RANGE), pos.add(RANGE, RANGE, RANGE));
        for (Entity e : world.getEntitiesInAABBexcluding(this, bounds, null)) {
            if (e.getDistanceSq(this) <= RANGE * RANGE && canEntityBeSeen(e)) {
                targets.add(e.getUniqueID());
            }
        }
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightMessage(getUniqueID(), targets));
        }
        super.livingTick();
    }

    @Override
    public void setOwnerID(@Nonnull UUID id) {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new RemoveObserverDataMessage(getUniqueID()));
        }
        super.setOwnerID(id);
    }

    @Override
    public void onRemovedFromWorld() {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new RemoveObserverDataMessage(getUniqueID()));
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

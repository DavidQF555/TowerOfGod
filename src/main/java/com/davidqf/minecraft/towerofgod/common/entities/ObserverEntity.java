package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.common.packets.ObserverAddHighlightMessage;
import com.davidqf.minecraft.towerofgod.common.packets.ObserverRemoveHighlightMessage;
import com.davidqf.minecraft.towerofgod.common.packets.RemoveObserverDataMessage;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
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
import java.util.List;
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
        Vector3d pos = getPositionVec();
        AxisAlignedBB bounds = new AxisAlignedBB(pos.subtract(RANGE, RANGE, RANGE), pos.add(RANGE, RANGE, RANGE));
        List<UUID> add = new ArrayList<>();
        for (Entity e : world.getEntitiesInAABBexcluding(this, bounds, null)) {
            if (e.getDistanceSq(this) <= RANGE * RANGE && canEntityBeSeen(e)) {
                add.add(e.getUniqueID());
            }
        }
        List<UUID> remove = (List<UUID>) targets.clone();
        remove.removeAll(add);
        add.removeAll(targets);
        targets.removeAll(remove);
        targets.addAll(add);
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            UUID id = getUniqueID();
            ObserverRemoveHighlightMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverRemoveHighlightMessage(id, remove));
            ObserverAddHighlightMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverAddHighlightMessage(id, add));
        }
        super.livingTick();
    }

    @Override
    public void setOwnerID(@Nonnull UUID id) {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            RemoveObserverDataMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new RemoveObserverDataMessage(getUniqueID()));
        }
        super.setOwnerID(id);
    }

    @Override
    public void onRemovedFromWorld() {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            RemoveObserverDataMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new RemoveObserverDataMessage(getUniqueID()));
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

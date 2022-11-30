package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ObserverChangeHighlightPacket;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@MethodsReturnNonnullByDefault
public class ObserverEntity extends FlyingDevice {

    private static final double RANGE = 24;
    private final Set<UUID> targets;

    public ObserverEntity(EntityType<ObserverEntity> type, Level world) {
        super(type, world);
        targets = new HashSet<>();
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 10);
    }

    @Override
    public void aiStep() {
        targets.clear();
        AABB bounds = AABB.ofSize(position(), RANGE * 2, RANGE * 2, RANGE * 2);
        for (Entity entity : level.getEntities(this, bounds, target -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && distanceToSqr(target) <= RANGE * RANGE && hasLineOfSight(target))) {
            targets.add(entity.getUUID());
        }
        Entity owner = getOwner();
        if (owner instanceof ServerPlayer) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner), new ObserverChangeHighlightPacket(getUUID(), targets));
        }
        super.aiStep();
    }

    @Override
    protected ItemStack getDeviceItem() {
        return ItemRegistry.OBSERVER.get().getDefaultInstance();
    }

    @Override
    public void setOwnerID(@Nonnull UUID id) {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayer) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner), new ObserverChangeHighlightPacket(getUUID(), new HashSet<>()));
        }
        super.setOwnerID(id);
    }

    @Override
    public void onRemovedFromWorld() {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayer) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner), new ObserverChangeHighlightPacket(getUUID(), new HashSet<>()));
        }
        super.onRemovedFromWorld();
    }

}

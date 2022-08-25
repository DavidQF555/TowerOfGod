package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ObserverChangeHighlightPacket;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@MethodsReturnNonnullByDefault
public class ObserverEntity extends FlyingDevice {

    private static final double RANGE = 24;
    private final Set<UUID> targets;

    public ObserverEntity(EntityType<ObserverEntity> type, World world) {
        super(type, world);
        targets = new HashSet<>();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 10);
    }

    @Override
    public void aiStep() {
        targets.clear();
        AxisAlignedBB bounds = AxisAlignedBB.unitCubeFromLowerCorner(position()).inflate(RANGE);
        for (Entity entity : level.getEntities(this, bounds, target -> EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(target) && distanceToSqr(target) <= RANGE * RANGE && canSee(target))) {
            targets.add(entity.getUUID());
        }
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUUID(), targets));
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
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUUID(), new HashSet<>()));
        }
        super.setOwnerID(id);
    }

    @Override
    public void onRemovedFromWorld() {
        Entity owner = getOwner();
        if (owner instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new ObserverChangeHighlightPacket(getUUID(), new HashSet<>()));
        }
        super.onRemovedFromWorld();
    }

}

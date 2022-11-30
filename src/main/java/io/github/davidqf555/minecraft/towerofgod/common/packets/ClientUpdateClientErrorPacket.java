package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateClientErrorPacket {

    private static final BiConsumer<ClientUpdateClientErrorPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
    };
    private static final Function<FriendlyByteBuf, ClientUpdateClientErrorPacket> DECODER = buffer -> new ClientUpdateClientErrorPacket();
    private static final BiConsumer<ClientUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            Vec3 eye = player.getEyePosition(1);
            EntityHitResult result = ProjectileUtil.getEntityHitResult(player.level, player, eye, eye.add(player.getLookAngle().scale(ShinsuTechniqueData.CAST_TARGET_RANGE)), AABB.ofSize(eye, ShinsuTechniqueData.CAST_TARGET_RANGE, ShinsuTechniqueData.CAST_TARGET_RANGE, ShinsuTechniqueData.CAST_TARGET_RANGE), null);
            Entity target = result == null ? null : result.getEntity();
            Map<ShinsuTechnique, Component> errors = new HashMap<>();
            for (ShinsuTechnique technique : ShinsuTechniqueRegistry.getRegistry()) {
                technique.create(player, target, player.getLookAngle()).ifRight(error -> errors.put(technique, error));
            }
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerUpdateClientErrorPacket(errors));
        });
        context.setPacketHandled(true);
    }
}

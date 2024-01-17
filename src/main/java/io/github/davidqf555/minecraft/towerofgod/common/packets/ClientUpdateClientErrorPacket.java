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
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientUpdateClientErrorPacket {

    private static final BiConsumer<ClientUpdateClientErrorPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeResourceLocation(message.technique.getId());
    private static final Function<FriendlyByteBuf, ClientUpdateClientErrorPacket> DECODER = buffer -> new ClientUpdateClientErrorPacket(ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation()));

    private final ShinsuTechnique technique;

    public ClientUpdateClientErrorPacket(ShinsuTechnique technique) {
        this.technique = technique;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ClientUpdateClientErrorPacket.class, index, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ClientUpdateClientErrorPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
            Vec3 eye = player.getEyePosition(1);
            EntityHitResult result = ProjectileUtil.getEntityHitResult(player.level(), player, eye, eye.add(player.getLookAngle().scale(ShinsuTechniqueData.CAST_TARGET_RANGE)), AABB.ofSize(eye, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2), entity -> true);
            Entity target = result == null ? null : result.getEntity();
            Optional<Component> error = technique.create(player, target, player.getLookAngle()).right();
        TowerOfGod.CHANNEL.send(new ServerUpdateClientErrorPacket(error), PacketDistributor.PLAYER.with(player));
    }
}

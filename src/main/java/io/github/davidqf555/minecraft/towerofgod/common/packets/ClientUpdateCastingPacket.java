package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateCastingPacket {

    private static final BiConsumer<ClientUpdateCastingPacket, PacketBuffer> ENCODER = (message, buffer) -> {
    };
    private static final Function<PacketBuffer, ClientUpdateCastingPacket> DECODER = buffer -> new ClientUpdateCastingPacket();
    private static final BiConsumer<ClientUpdateCastingPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateCastingPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new ServerUpdateCastingPacket(player.getId(), ClientReference.isCasting(player)));
        });
        context.setPacketHandled(true);
    }

}

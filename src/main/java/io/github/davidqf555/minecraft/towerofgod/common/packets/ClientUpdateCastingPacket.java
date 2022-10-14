package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.CastingData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateCastingPacket {

    private static final BiConsumer<ClientUpdateCastingPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.casting);
    };
    private static final Function<FriendlyByteBuf, ClientUpdateCastingPacket> DECODER = buffer -> new ClientUpdateCastingPacket(buffer.readBoolean());
    private static final BiConsumer<ClientUpdateCastingPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final boolean casting;

    public ClientUpdateCastingPacket(boolean casting) {
        this.casting = casting;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateCastingPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            CastingData data = CastingData.get(player);
            data.setCasting(casting);
            TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ServerUpdateCastingPacket(player.getId(), casting));
        });
        context.setPacketHandled(true);
    }

}

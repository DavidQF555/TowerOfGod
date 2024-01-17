package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.CastingData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientUpdateCastingPacket {

    private static final BiConsumer<ClientUpdateCastingPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeBoolean(message.casting);
    private static final Function<FriendlyByteBuf, ClientUpdateCastingPacket> DECODER = buffer -> new ClientUpdateCastingPacket(buffer.readBoolean());

    private final boolean casting;

    public ClientUpdateCastingPacket(boolean casting) {
        this.casting = casting;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ClientUpdateCastingPacket.class, index, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ClientUpdateCastingPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
            CastingData data = CastingData.get(player);
            data.setCasting(casting);
        TowerOfGod.CHANNEL.send(new ServerUpdateCastingPacket(player.getId(), casting), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(player));
    }

}

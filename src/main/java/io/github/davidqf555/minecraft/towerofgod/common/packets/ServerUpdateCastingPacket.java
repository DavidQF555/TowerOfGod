package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerUpdateCastingPacket {

    private static final BiConsumer<ServerUpdateCastingPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.casting);
    };
    private static final Function<FriendlyByteBuf, ServerUpdateCastingPacket> DECODER = buffer -> new ServerUpdateCastingPacket(buffer.readInt(), buffer.readBoolean());

    private final int id;
    private final boolean casting;

    public ServerUpdateCastingPacket(int id, boolean casting) {
        this.id = id;
        this.casting = casting;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ServerUpdateCastingPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ServerUpdateCastingPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.handleUpdateCastingPacket(id, casting);
    }

}

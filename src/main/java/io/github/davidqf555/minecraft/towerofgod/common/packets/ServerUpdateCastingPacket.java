package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateCastingPacket {

    private static final BiConsumer<ServerUpdateCastingPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.casting);
    };
    private static final Function<PacketBuffer, ServerUpdateCastingPacket> DECODER = buffer -> new ServerUpdateCastingPacket(buffer.readInt(), buffer.readBoolean());
    private static final BiConsumer<ServerUpdateCastingPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int id;
    private final boolean casting;

    public ServerUpdateCastingPacket(int id, boolean casting) {
        this.id = id;
        this.casting = casting;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateCastingPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.handleUpdateCastingPacket(id, casting));
        context.setPacketHandled(true);
    }

}

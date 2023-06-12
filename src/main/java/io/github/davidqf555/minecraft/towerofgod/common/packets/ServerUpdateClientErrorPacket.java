package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateClientErrorPacket {

    private static final BiConsumer<ServerUpdateClientErrorPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.error.isPresent());
        message.error.ifPresent(buffer::writeComponent);
    };
    private static final Function<PacketBuffer, ServerUpdateClientErrorPacket> DECODER = buffer -> new ServerUpdateClientErrorPacket(buffer.readBoolean() ? Optional.of(buffer.readComponent()) : Optional.empty());
    private static final BiConsumer<ServerUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Optional<ITextComponent> error;

    public ServerUpdateClientErrorPacket(Optional<ITextComponent> error) {
        this.error = error;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.error = error);
        context.setPacketHandled(true);
    }
}

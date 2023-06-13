package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateClientErrorPacket {

    private static final BiConsumer<ServerUpdateClientErrorPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.error.isPresent());
        message.error.ifPresent(buffer::writeComponent);
    };
    private static final Function<FriendlyByteBuf, ServerUpdateClientErrorPacket> DECODER = buffer -> new ServerUpdateClientErrorPacket(buffer.readBoolean() ? Optional.of(buffer.readComponent()) : Optional.empty());
    private static final BiConsumer<ServerUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final Optional<Component> error;

    public ServerUpdateClientErrorPacket(Optional<Component> error) {
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

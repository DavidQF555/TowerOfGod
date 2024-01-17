package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerUpdateClientErrorPacket {

    private static final BiConsumer<ServerUpdateClientErrorPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.error.isPresent());
        message.error.ifPresent(buffer::writeComponent);
    };
    private static final Function<FriendlyByteBuf, ServerUpdateClientErrorPacket> DECODER = buffer -> new ServerUpdateClientErrorPacket(buffer.readBoolean() ? Optional.of(buffer.readComponent()) : Optional.empty());

    private final Optional<Component> error;

    public ServerUpdateClientErrorPacket(Optional<Component> error) {
        this.error = error;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ServerUpdateClientErrorPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ServerUpdateClientErrorPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.error = error;
    }
}

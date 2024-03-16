package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateCastingPacket {

    private static final BiConsumer<UpdateCastingPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeUUID(message.id);
        buffer.writeBoolean(message.casting);
    };
    private static final Function<FriendlyByteBuf, UpdateCastingPacket> DECODER = buffer -> new UpdateCastingPacket(buffer.readUUID(), buffer.readBoolean());
    private static final BiConsumer<UpdateCastingPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final UUID id;
    private final boolean casting;

    public UpdateCastingPacket(UUID id, boolean casting) {
        this.id = id;
        this.casting = casting;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateCastingPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.handleUpdateCastingPacket(id, casting));
        context.setPacketHandled(true);
    }

}

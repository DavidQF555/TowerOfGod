package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateMaxBaangsPacket {

    private static final BiConsumer<UpdateMaxBaangsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.baangs);
    };
    private static final Function<FriendlyByteBuf, UpdateMaxBaangsPacket> DECODER = buffer -> new UpdateMaxBaangsPacket(buffer.readInt());
    private static final BiConsumer<UpdateMaxBaangsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final int baangs;

    public UpdateMaxBaangsPacket(int baangs) {
        this.baangs = baangs;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateMaxBaangsPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.maxBaangs = baangs);
        context.setPacketHandled(true);
    }

}

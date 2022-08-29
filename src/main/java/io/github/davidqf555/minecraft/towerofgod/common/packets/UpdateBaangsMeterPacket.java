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

public class UpdateBaangsMeterPacket {

    private static final BiConsumer<UpdateBaangsMeterPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.baangs);
        buffer.writeInt(message.maxBaangs);
    };
    private static final Function<FriendlyByteBuf, UpdateBaangsMeterPacket> DECODER = buffer -> new UpdateBaangsMeterPacket(buffer.readInt(), buffer.readInt());
    private static final BiConsumer<UpdateBaangsMeterPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int baangs;
    private final int maxBaangs;

    public UpdateBaangsMeterPacket(int baangs, int maxBaangs) {
        this.baangs = baangs;
        this.maxBaangs = maxBaangs;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateBaangsMeterPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.BAANGS.setValue(baangs);
            ClientReference.BAANGS.setMax(maxBaangs);
        });
        context.setPacketHandled(true);
    }

}

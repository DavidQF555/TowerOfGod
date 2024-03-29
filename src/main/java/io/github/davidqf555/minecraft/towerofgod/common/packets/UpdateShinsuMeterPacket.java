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

public class UpdateShinsuMeterPacket {

    private static final BiConsumer<UpdateShinsuMeterPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.shinsu);
        buffer.writeInt(message.maxShinsu);
    };
    private static final Function<FriendlyByteBuf, UpdateShinsuMeterPacket> DECODER = buffer -> new UpdateShinsuMeterPacket(buffer.readInt(), buffer.readInt());
    private static final BiConsumer<UpdateShinsuMeterPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int shinsu;
    private final int maxShinsu;

    public UpdateShinsuMeterPacket(int shinsu, int maxShinsu) {
        this.shinsu = shinsu;
        this.maxShinsu = maxShinsu;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateShinsuMeterPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.SHINSU.setValue(shinsu);
            ClientReference.SHINSU.setMax(maxShinsu);
        });
        context.setPacketHandled(true);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientQualityPacket {

    private static final BiConsumer<UpdateClientQualityPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.quality.ordinal());
    };
    private static final Function<PacketBuffer, UpdateClientQualityPacket> DECODER = buffer -> new UpdateClientQualityPacket(ShinsuQuality.values()[buffer.readInt()]);
    private static final BiConsumer<UpdateClientQualityPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuQuality quality;

    public UpdateClientQualityPacket(ShinsuQuality quality) {
        this.quality = quality;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientQualityPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.quality = quality);
            context.setPacketHandled(true);
        }
    }
}

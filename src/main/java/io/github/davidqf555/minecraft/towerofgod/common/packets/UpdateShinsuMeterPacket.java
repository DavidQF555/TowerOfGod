package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateShinsuMeterPacket {

    private static final BiConsumer<UpdateShinsuMeterPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.shinsu);
        buffer.writeInt(message.maxShinsu);
    };
    private static final Function<PacketBuffer, UpdateShinsuMeterPacket> DECODER = buffer -> new UpdateShinsuMeterPacket(buffer.readInt(), buffer.readInt());
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
        TowerOfGod.CHANNEL.registerMessage(index, UpdateShinsuMeterPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.shinsu.setValue(shinsu);
                ClientReference.shinsu.setMax(maxShinsu);
            });
            context.setPacketHandled(true);
        }
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientShinsuQualityShapePacket {

    private static final BiConsumer<UpdateClientShinsuQualityShapePacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.quality.ordinal());
        buffer.writeInt(message.shape.ordinal());
    };
    private static final Function<PacketBuffer, UpdateClientShinsuQualityShapePacket> DECODER = buffer -> new UpdateClientShinsuQualityShapePacket(ShinsuQuality.values()[buffer.readInt()], ShinsuShape.values()[buffer.readInt()]);
    private static final BiConsumer<UpdateClientShinsuQualityShapePacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuQuality quality;
    private final ShinsuShape shape;

    public UpdateClientShinsuQualityShapePacket(ShinsuQuality quality, ShinsuShape shape) {
        this.quality = quality;
        this.shape = shape;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientShinsuQualityShapePacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.quality = quality;
                ClientReference.shape = shape;
            });
            context.setPacketHandled(true);
        }
    }
}

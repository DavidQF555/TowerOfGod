package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientQualityPacket {

    private static final BiConsumer<UpdateClientQualityPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.quality == null);
        if (message.quality != null) {
            buffer.writeResourceLocation(message.quality.getRegistryName());
        }
    };
    private static final Function<PacketBuffer, UpdateClientQualityPacket> DECODER = buffer -> new UpdateClientQualityPacket(buffer.readBoolean() ? null : ShinsuQualityRegistry.getRegistry().getValue(buffer.readResourceLocation()));
    private static final BiConsumer<UpdateClientQualityPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuQuality quality;

    public UpdateClientQualityPacket(@Nullable ShinsuQuality quality) {
        this.quality = quality;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientQualityPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.quality = quality);
        context.setPacketHandled(true);
    }
}

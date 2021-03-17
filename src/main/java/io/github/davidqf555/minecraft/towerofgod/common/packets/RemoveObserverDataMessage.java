package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ObserverEventBusSubscriber;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemoveObserverDataMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "remove_observer_data_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<RemoveObserverDataMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeUniqueId(message.id);
    };
    private static final Function<PacketBuffer, RemoveObserverDataMessage> DECODER = buffer -> new RemoveObserverDataMessage(buffer.readUniqueId());
    private static final BiConsumer<RemoveObserverDataMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final UUID id;

    public RemoveObserverDataMessage(UUID id) {
        this.id = id;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, RemoveObserverDataMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ObserverEventBusSubscriber.highlight.remove(id);
                ObserverEventBusSubscriber.stopHighlight.remove(id);
            });
            context.setPacketHandled(true);
        }
    }
}

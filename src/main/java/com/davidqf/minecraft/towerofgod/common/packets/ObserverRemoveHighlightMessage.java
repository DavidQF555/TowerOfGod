package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.util.ClientEventBusSubscriber;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObserverRemoveHighlightMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "remove_highlight_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ObserverRemoveHighlightMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeUniqueId(message.id);
        buffer.writeInt(message.entities.size());
        for (UUID id : message.entities) {
            buffer.writeUniqueId(id);
        }
    };
    private static final Function<PacketBuffer, ObserverRemoveHighlightMessage> DECODER = buffer -> {
        UUID id = buffer.readUniqueId();
        List<UUID> entities = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            entities.add(buffer.readUniqueId());
        }
        return new ObserverRemoveHighlightMessage(id, entities);
    };
    private static final BiConsumer<ObserverRemoveHighlightMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, ObserverRemoveHighlightMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private final UUID id;
    private final List<UUID> entities;

    public ObserverRemoveHighlightMessage(UUID id, List<UUID> entities) {
        this.id = id;
        this.entities = entities;
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                if (ClientEventBusSubscriber.stopHighlight.containsKey(id)) {
                    ClientEventBusSubscriber.stopHighlight.get(id).addAll(entities);
                } else {
                    ClientEventBusSubscriber.stopHighlight.put(id, entities);
                }
            });
            context.setPacketHandled(true);
        }
    }
}

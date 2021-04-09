package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ObserverEventBusSubscriber;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObserverChangeHighlightMessage {

    private static final BiConsumer<ObserverChangeHighlightMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeUniqueId(message.id);
        buffer.writeInt(message.entities.size());
        for (UUID id : message.entities) {
            buffer.writeUniqueId(id);
        }
    };
    private static final Function<PacketBuffer, ObserverChangeHighlightMessage> DECODER = buffer -> {
        UUID id = buffer.readUniqueId();
        List<UUID> entities = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            entities.add(buffer.readUniqueId());
        }
        return new ObserverChangeHighlightMessage(id, entities);
    };
    private static final BiConsumer<ObserverChangeHighlightMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final UUID id;
    private final List<UUID> entities;

    public ObserverChangeHighlightMessage(UUID id, List<UUID> entities) {
        this.id = id;
        this.entities = entities;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ObserverChangeHighlightMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                List<UUID> highlight;
                if (ObserverEventBusSubscriber.highlight.containsKey(id)) {
                    highlight = ObserverEventBusSubscriber.highlight.get(id);
                    highlight.addAll(getUnique(highlight, entities));
                } else {
                    highlight = entities;
                    ObserverEventBusSubscriber.highlight.put(id, entities);
                }
                List<UUID> remove = new ArrayList<>();
                for (UUID id : highlight) {
                    if (!entities.contains(id)) {
                        remove.add(id);
                    }
                }
                if (ObserverEventBusSubscriber.stopHighlight.containsKey(id)) {
                    List<UUID> stop = ObserverEventBusSubscriber.stopHighlight.get(id);
                    stop.addAll(getUnique(stop, remove));
                } else {
                    ObserverEventBusSubscriber.stopHighlight.put(id, remove);
                }
            });
            context.setPacketHandled(true);
        }
    }

    private List<UUID> getUnique(List<UUID> original, List<UUID> newer) {
        List<UUID> unique = new ArrayList<>();
        for (UUID id : newer) {
            if (!original.contains(id)) {
                unique.add(id);
            }
        }
        return unique;
    }
}

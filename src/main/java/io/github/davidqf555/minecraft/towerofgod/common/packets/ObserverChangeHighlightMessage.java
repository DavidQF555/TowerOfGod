package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ObserverEventBusSubscriber;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
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
        Set<UUID> entities = new HashSet<>();
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
    private final Set<UUID> entities;

    public ObserverChangeHighlightMessage(UUID id, Set<UUID> entities) {
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
                if (ObserverEventBusSubscriber.startStopHighlight.containsKey(id)) {
                    Pair<Set<UUID>, Set<UUID>> pair = ObserverEventBusSubscriber.startStopHighlight.get(id);
                    Set<UUID> highlight = pair.getFirst();
                    Set<UUID> remove = pair.getSecond();
                    remove.removeAll(entities);
                    highlight.stream().filter(uuid -> !entities.contains(uuid)).forEach(remove::add);
                    highlight.addAll(entities);
                } else {
                    ObserverEventBusSubscriber.startStopHighlight.put(id, Pair.of(entities, new HashSet<>()));
                }
            });
            context.setPacketHandled(true);
        }
    }
}

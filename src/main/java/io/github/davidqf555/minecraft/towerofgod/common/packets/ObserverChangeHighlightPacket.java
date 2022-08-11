package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.events.ObserverEventBusSubscriber;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObserverChangeHighlightPacket {

    private static final BiConsumer<ObserverChangeHighlightPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeUniqueId(message.id);
        buffer.writeInt(message.entities.size());
        for (UUID id : message.entities) {
            buffer.writeUniqueId(id);
        }
    };
    private static final Function<PacketBuffer, ObserverChangeHighlightPacket> DECODER = buffer -> {
        UUID id = buffer.readUniqueId();
        Set<UUID> entities = new HashSet<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            entities.add(buffer.readUniqueId());
        }
        return new ObserverChangeHighlightPacket(id, entities);
    };
    private static final BiConsumer<ObserverChangeHighlightPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final UUID id;
    private final Set<UUID> entities;

    public ObserverChangeHighlightPacket(UUID id, Set<UUID> entities) {
        this.id = id;
        this.entities = entities;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ObserverChangeHighlightPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (ObserverEventBusSubscriber.START_STOP_HIGHLIGHT.containsKey(id)) {
                Pair<Set<UUID>, Set<UUID>> pair = ObserverEventBusSubscriber.START_STOP_HIGHLIGHT.get(id);
                Set<UUID> highlight = pair.getFirst();
                Set<UUID> remove = pair.getSecond();
                remove.removeAll(entities);
                highlight.stream().filter(uuid -> !entities.contains(uuid)).forEach(remove::add);
                highlight.addAll(entities);
            } else {
                ObserverEventBusSubscriber.START_STOP_HIGHLIGHT.put(id, Pair.of(entities, new HashSet<>()));
            }
        });
        context.setPacketHandled(true);
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientKnownMessage {

    private static final BiConsumer<UpdateClientKnownMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.known.size());
        for (ShinsuTechnique technique : message.known.keySet()) {
            buffer.writeString(technique.name());
            buffer.writeInt(message.known.get(technique));
        }
    };
    private static final Function<PacketBuffer, UpdateClientKnownMessage> DECODER = buffer -> {
        Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            known.put(ShinsuTechnique.valueOf(buffer.readString()), buffer.readInt());
        }
        return new UpdateClientKnownMessage(known);
    };
    private static final BiConsumer<UpdateClientKnownMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, Integer> known;

    public UpdateClientKnownMessage(Map<ShinsuTechnique, Integer> known) {
        this.known = known;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientKnownMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                ShinsuStats stats = ShinsuStats.get(player);
                Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    known.put(technique, stats.getTechniqueLevel(technique));
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientKnownMessage(known));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.known = known);
            context.setPacketHandled(true);
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
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

public class UpdateClientKnownPacket {

    private static final BiConsumer<UpdateClientKnownPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.known.size());
        for (ShinsuTechnique technique : message.known.keySet()) {
            buffer.writeString(technique.name());
            buffer.writeInt(message.known.get(technique));
        }
    };
    private static final Function<PacketBuffer, UpdateClientKnownPacket> DECODER = buffer -> {
        Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            known.put(ShinsuTechnique.valueOf(buffer.readString()), buffer.readInt());
        }
        return new UpdateClientKnownPacket(known);
    };
    private static final BiConsumer<UpdateClientKnownPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, Integer> known;

    public UpdateClientKnownPacket(Map<ShinsuTechnique, Integer> known) {
        this.known = known;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientKnownPacket.class, ENCODER, DECODER, CONSUMER);
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
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientKnownPacket(known));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.known = known);
            context.setPacketHandled(true);
        }
    }
}

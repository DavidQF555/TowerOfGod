package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
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

public class UpdateClientShinsuDataPacket {

    private static final BiConsumer<UpdateClientShinsuDataPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.data.size());
        for (Map.Entry<ShinsuTechniqueType, ShinsuTechniqueData> type : message.data.entrySet()) {
            buffer.writeString(type.getKey().name());
            buffer.writeCompoundTag(type.getValue().serializeNBT());
        }
    };
    private static final Function<PacketBuffer, UpdateClientShinsuDataPacket> DECODER = buffer -> {
        Map<ShinsuTechniqueType, ShinsuTechniqueData> data = new EnumMap<>(ShinsuTechniqueType.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechniqueType type = ShinsuTechniqueType.valueOf(buffer.readString());
            ShinsuTechniqueData d = new ShinsuTechniqueData();
            d.deserializeNBT(buffer.readCompoundTag());
            data.put(type, d);
        }
        return new UpdateClientShinsuDataPacket(data);
    };
    private static final BiConsumer<UpdateClientShinsuDataPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechniqueType, ShinsuTechniqueData> data;

    public UpdateClientShinsuDataPacket() {
        this(new EnumMap<>(ShinsuTechniqueType.class));
    }

    public UpdateClientShinsuDataPacket(Map<ShinsuTechniqueType, ShinsuTechniqueData> data) {
        this.data = data;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientShinsuDataPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                ShinsuStats stats = ShinsuStats.get(player);
                Map<ShinsuTechniqueType, ShinsuTechniqueData> data = new EnumMap<>(ShinsuTechniqueType.class);
                for (ShinsuTechniqueType type : ShinsuTechniqueType.values()) {
                    data.put(type, stats.getData(type));
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientShinsuDataPacket(data));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.data = data);
            context.setPacketHandled(true);
        }
    }
}

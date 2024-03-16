package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateBaangsPacket {

    private static final BiConsumer<ServerUpdateBaangsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.baangs.size());
        message.baangs.forEach((type, count) -> {
            buffer.writeResourceLocation(type.getId());
            buffer.writeInt(count);
        });
    };
    private static final Function<FriendlyByteBuf, ServerUpdateBaangsPacket> DECODER = buffer -> {
        Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ConfiguredShinsuTechniqueType<?, ?> type = ConfiguredTechniqueTypeRegistry.getRegistry().getValue(buffer.readResourceLocation());
            int count = buffer.readInt();
            baangs.put(type, count);
        }
        return new ServerUpdateBaangsPacket(baangs);
    };
    private static final BiConsumer<ServerUpdateBaangsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs;

    public ServerUpdateBaangsPacket(Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs) {
        this.baangs = baangs;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateBaangsPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.BAANGS.clear();
            ClientReference.BAANGS.putAll(baangs);
        });
        context.setPacketHandled(true);
    }

}

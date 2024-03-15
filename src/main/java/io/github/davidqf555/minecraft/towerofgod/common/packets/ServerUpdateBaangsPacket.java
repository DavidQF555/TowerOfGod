package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateBaangsPacket {

    private static final BiConsumer<ServerUpdateBaangsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.unlocked.size());
        message.unlocked.forEach(pair -> {
            buffer.writeResourceLocation(pair.getFirst().getRegistryName());
            buffer.writeInt(pair.getSecond());
        });
    };
    private static final Function<FriendlyByteBuf, ServerUpdateBaangsPacket> DECODER = buffer -> {
        List<Pair<ConfiguredShinsuTechniqueType<?, ?>, Integer>> baangs = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ConfiguredShinsuTechniqueType<?, ?> type = ConfiguredTechniqueTypeRegistry.getRegistry().getValue(buffer.readResourceLocation());
            int count = buffer.readInt();
            baangs.add(Pair.of(type, count));
        }
        return new ServerUpdateBaangsPacket(baangs);
    };
    private static final BiConsumer<ServerUpdateBaangsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final List<Pair<ConfiguredShinsuTechniqueType<?, ?>, Integer>> unlocked;

    public ServerUpdateBaangsPacket(Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> unlocked) {
        List<Pair<ConfiguredShinsuTechniqueType<?, ?>, Integer>> list = new ArrayList<>();
        unlocked.forEach((type, count) -> list.add(Pair.of(type, count)));
        this.unlocked = list;
    }

    public ServerUpdateBaangsPacket(List<Pair<ConfiguredShinsuTechniqueType<?, ?>, Integer>> unlocked) {
        this.unlocked = unlocked;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateBaangsPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.BAANGS.clear();
            ClientReference.BAANGS.addAll(unlocked);
        });
        context.setPacketHandled(true);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateUnlockedPacket {

    private static final BiConsumer<ServerUpdateUnlockedPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.unlocked.size());
        for (ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> technique : message.unlocked) {
            buffer.writeResourceLocation(technique.location());
        }
    };
    private static final Function<FriendlyByteBuf, ServerUpdateUnlockedPacket> DECODER = buffer -> {
        Set<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>> unlocked = new HashSet<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            unlocked.add(ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, buffer.readResourceLocation()));
        }
        return new ServerUpdateUnlockedPacket(unlocked);
    };
    private static final BiConsumer<ServerUpdateUnlockedPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Set<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>> unlocked;

    public ServerUpdateUnlockedPacket(Set<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>> unlocked) {
        this.unlocked = unlocked;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateUnlockedPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.UNLOCKED.clear();
            ClientReference.UNLOCKED.addAll(unlocked);
        });
        context.setPacketHandled(true);
    }

}

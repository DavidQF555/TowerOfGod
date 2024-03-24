package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateBaangsPacket {

    private static final BiConsumer<ClientUpdateBaangsPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.unlocked.size());
        message.unlocked.forEach((type, count) -> {
            buffer.writeResourceLocation(type.location());
            buffer.writeInt(count);
        });
    };
    private static final Function<FriendlyByteBuf, ClientUpdateBaangsPacket> DECODER = buffer -> {
        Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> baangs = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> type = ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, buffer.readResourceLocation());
            int count = buffer.readInt();
            baangs.put(type, count);
        }
        return new ClientUpdateBaangsPacket(baangs);
    };
    private static final BiConsumer<ClientUpdateBaangsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> unlocked;

    public ClientUpdateBaangsPacket(Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> unlocked) {
        this.unlocked = unlocked;
    }

    public ClientUpdateBaangsPacket(Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer>[] unlocked) {
        Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> map = new HashMap<>();
        for (Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> pair : unlocked) {
            map.put(pair.getFirst(), map.getOrDefault(pair.getFirst(), 0) + pair.getSecond());
        }
        this.unlocked = map;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateBaangsPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            PlayerTechniqueData data = PlayerTechniqueData.get(player);
            data.setBaangs(unlocked);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerUpdateBaangsPacket(data.getBaangSettings()));
        });
        context.setPacketHandled(true);
    }

}

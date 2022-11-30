package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientOpenCombinationGUIPacket {

    private static final BiConsumer<ClientOpenCombinationGUIPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
    };
    private static final Function<FriendlyByteBuf, ClientOpenCombinationGUIPacket> DECODER = buffer -> new ClientOpenCombinationGUIPacket();
    private static final BiConsumer<ClientOpenCombinationGUIPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientOpenCombinationGUIPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            Set<ShinsuTechnique> unlocked = PlayerTechniqueData.get(player).getUnlockedTechniques(player);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerOpenCombinationGUIPacket(unlocked));
        });
        context.setPacketHandled(true);
    }
}

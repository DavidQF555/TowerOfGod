package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientOpenCombinationGUIPacket {

    private static final BiConsumer<ClientOpenCombinationGUIPacket, PacketBuffer> ENCODER = (message, buffer) -> {
    };
    private static final Function<PacketBuffer, ClientOpenCombinationGUIPacket> DECODER = buffer -> new ClientOpenCombinationGUIPacket();
    private static final BiConsumer<ClientOpenCombinationGUIPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientOpenCombinationGUIPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            Set<ShinsuTechnique> unlocked = PlayerTechniqueData.get(player).getUnlockedTechniques(player);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerOpenCombinationGUIPacket(unlocked));
        });
        context.setPacketHandled(true);
    }
}

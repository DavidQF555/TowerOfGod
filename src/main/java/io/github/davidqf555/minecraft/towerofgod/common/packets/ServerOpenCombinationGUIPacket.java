package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerOpenCombinationGUIPacket {

    private static final BiConsumer<ServerOpenCombinationGUIPacket, PacketBuffer> ENCODER = (message, buffer) -> {
    };
    private static final Function<PacketBuffer, ServerOpenCombinationGUIPacket> DECODER = buffer -> new ServerOpenCombinationGUIPacket();
    private static final BiConsumer<ServerOpenCombinationGUIPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerOpenCombinationGUIPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(ClientReference::openCombinationGUI);
        context.setPacketHandled(true);
    }
}

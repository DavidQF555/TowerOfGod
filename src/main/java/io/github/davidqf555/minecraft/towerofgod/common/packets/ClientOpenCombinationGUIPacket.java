package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ClientOpenCombinationGUIPacket {

    private static final BiConsumer<ClientOpenCombinationGUIPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
    };
    private static final Function<FriendlyByteBuf, ClientOpenCombinationGUIPacket> DECODER = buffer -> new ClientOpenCombinationGUIPacket();

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ClientOpenCombinationGUIPacket.class, index, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ClientOpenCombinationGUIPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        TowerOfGod.CHANNEL.send(new ServerOpenCombinationGUIPacket(PlayerTechniqueData.get(player).getUsable(player)), PacketDistributor.PLAYER.with(player));
    }

}

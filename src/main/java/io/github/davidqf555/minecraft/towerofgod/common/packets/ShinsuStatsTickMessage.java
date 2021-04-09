package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuStatsTickMessage {

    private static final BiConsumer<ShinsuStatsTickMessage, PacketBuffer> ENCODER = (message, buffer) -> {

    };
    private static final Function<PacketBuffer, ShinsuStatsTickMessage> DECODER = buffer -> new ShinsuStatsTickMessage();

    private static final BiConsumer<ShinsuStatsTickMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ShinsuStatsTickMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IShinsuStats tar = IShinsuStats.get(player);
                tar.tick(player.getServerWorld());
            });
            context.setPacketHandled(true);
        }
    }
}

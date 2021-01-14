package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuStatsTickMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats_tick_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ShinsuStatsTickMessage, PacketBuffer> ENCODER = (message, buffer) -> {

    };
    private static final Function<PacketBuffer, ShinsuStatsTickMessage> DECODER = buffer -> new ShinsuStatsTickMessage();

    private static final BiConsumer<ShinsuStatsTickMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, ShinsuStatsTickMessage.class, ENCODER, DECODER, CONSUMER);
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

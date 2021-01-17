package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.gui.GuiEventBusSubscriber;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateStatsMetersMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "update_meter_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<UpdateStatsMetersMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.shinsu);
        buffer.writeInt(message.maxShinsu);
        buffer.writeInt(message.baangs);
        buffer.writeInt(message.maxBaangs);
    };
    private static final Function<PacketBuffer, UpdateStatsMetersMessage> DECODER = buffer -> new UpdateStatsMetersMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    private static final BiConsumer<UpdateStatsMetersMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int shinsu;
    private final int maxShinsu;
    private final int baangs;
    private final int maxBaangs;

    public UpdateStatsMetersMessage(int shinsu, int maxShinsu, int baangs, int maxBaangs) {
        this.shinsu = shinsu;
        this.maxShinsu = maxShinsu;
        this.baangs = baangs;
        this.maxBaangs = maxBaangs;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, UpdateStatsMetersMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IShinsuStats stats = IShinsuStats.get(player);
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                GuiEventBusSubscriber.shinsu.setValue(shinsu);
                GuiEventBusSubscriber.shinsu.setMax(maxShinsu);
                GuiEventBusSubscriber.baangs.setValue(baangs);
                GuiEventBusSubscriber.baangs.setMax(maxBaangs);
            });
            context.setPacketHandled(true);
        }
    }

}

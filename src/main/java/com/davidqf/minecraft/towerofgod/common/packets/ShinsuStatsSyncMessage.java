package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuStatsSyncMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_sync_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ShinsuStatsSyncMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeCompoundTag(message.stats);
    };
    private static final Function<PacketBuffer, ShinsuStatsSyncMessage> DECODER = buffer -> {
        IShinsuStats.ShinsuStats stats = new IShinsuStats.ShinsuStats();
            stats.deserialize(buffer.readCompoundTag());
        return new ShinsuStatsSyncMessage(stats);
    };
    private static final BiConsumer<ShinsuStatsSyncMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        context.get().enqueueWork(() -> message.handle(context.get()));
    };

    private final CompoundNBT stats;

    public ShinsuStatsSyncMessage(IShinsuStats stats){
        this.stats = stats.serialize();
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, ShinsuStatsSyncMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
     NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            IShinsuStats tar = IShinsuStats.get(player);
            tar.deserialize(stats);
            context.setPacketHandled(true);
        }
        else if(dir == NetworkDirection.PLAY_TO_CLIENT){
            IShinsuStats tar = IShinsuStats.get(Minecraft.getInstance().player);
            tar.deserialize(stats);
            context.setPacketHandled(true);
        }
    }

}

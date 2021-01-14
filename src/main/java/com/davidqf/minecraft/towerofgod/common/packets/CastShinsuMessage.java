package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "cast_technique_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<CastShinsuMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.technique.getName());
        boolean contains = message.target != null;
        buffer.writeBoolean(contains);
        if (contains) {
            buffer.writeUniqueId(message.target);
        }
    };
    private static final Function<PacketBuffer, CastShinsuMessage> DECODER = buffer -> {
        ShinsuTechnique technique = ShinsuTechnique.get(buffer.readString());
        UUID target = null;
        if (buffer.readBoolean()) {
            target = buffer.readUniqueId();
        }
        return new CastShinsuMessage(technique, target);
    };
    private static final BiConsumer<CastShinsuMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique technique;
    private final UUID target;

    public CastShinsuMessage(ShinsuTechnique technique, @Nullable UUID target) {
        this.technique = technique;
        this.target = target;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, CastShinsuMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                Entity t = player.getServerWorld().getEntityByUuid(target);
                IShinsuStats stats = IShinsuStats.get(player);
                stats.cast(player, technique, t, player.getLookVec());
            });
            context.setPacketHandled(true);
        }
    }

}

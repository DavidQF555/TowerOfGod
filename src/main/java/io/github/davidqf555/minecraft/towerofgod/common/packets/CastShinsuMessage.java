package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuMessage {

    private static final BiConsumer<CastShinsuMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.technique.name());
        boolean contains = message.target != null;
        buffer.writeBoolean(contains);
        if (contains) {
            buffer.writeUniqueId(message.target);
        }
    };
    private static final Function<PacketBuffer, CastShinsuMessage> DECODER = buffer -> {
        ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
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
        TowerOfGod.CHANNEL.registerMessage(index, CastShinsuMessage.class, ENCODER, DECODER, CONSUMER);
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

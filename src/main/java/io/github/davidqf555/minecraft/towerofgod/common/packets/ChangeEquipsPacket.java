package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.PlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeEquipsPacket {

    private static final BiConsumer<ChangeEquipsPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.size());
        for (Pair<ShinsuTechnique, String> pair : message.equipped) {
            buffer.writeString(pair.getFirst().name());
            buffer.writeString(pair.getSecond());
        }
    };
    private static final Function<PacketBuffer, ChangeEquipsPacket> DECODER = buffer -> {
        List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            equipped.add(Pair.of(ShinsuTechnique.valueOf(buffer.readString()), buffer.readString()));
        }
        return new ChangeEquipsPacket(equipped);
    };
    private static final BiConsumer<ChangeEquipsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final List<Pair<ShinsuTechnique, String>> equipped;

    public ChangeEquipsPacket(List<Pair<ShinsuTechnique, String>> equipped) {
        this.equipped = equipped;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ChangeEquipsPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                PlayerShinsuEquips tar = PlayerShinsuEquips.get(player);
                tar.setEquipped(equipped);
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.equipped = equipped;
            });
            context.setPacketHandled(true);
        }
    }

}

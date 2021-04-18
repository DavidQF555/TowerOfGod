package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeEquipsMessage {

    private static final BiConsumer<ChangeEquipsMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.length);
        for (ShinsuTechnique technique : message.equipped) {
            buffer.writeString(technique == null ? "" : technique.name());
        }
    };
    private static final Function<PacketBuffer, ChangeEquipsMessage> DECODER = buffer -> {
        ShinsuTechnique[] techniques = new ShinsuTechnique[4];
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            try {
                techniques[i] = ShinsuTechnique.valueOf(buffer.readString());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new ChangeEquipsMessage(techniques);
    };
    private static final BiConsumer<ChangeEquipsMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique[] equipped;

    public ChangeEquipsMessage(ShinsuTechnique[] equipped) {
        this.equipped = equipped;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ChangeEquipsMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IPlayerShinsuEquips tar = IPlayerShinsuEquips.get(player);
                tar.setEquipped(equipped);
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientEquippedMessage(tar.getEquipped()));
            });
            context.setPacketHandled(true);
        }
    }

}

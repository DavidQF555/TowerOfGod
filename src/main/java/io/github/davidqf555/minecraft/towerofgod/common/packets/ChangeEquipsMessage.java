package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeEquipsMessage {

    private static final BiConsumer<ChangeEquipsMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.length);
        for (ShinsuTechnique technique : message.equipped) {
            buffer.writeString(technique == null ? "" : technique.name());
        }
        buffer.writeInt(message.settings.length);
        for (String settings : message.settings) {
            buffer.writeString(settings == null ? "" : settings);
        }
    };
    private static final Function<PacketBuffer, ChangeEquipsMessage> DECODER = buffer -> {
        int size = buffer.readInt();
        ShinsuTechnique[] techniques = new ShinsuTechnique[size];
        for (int i = 0; i < size; i++) {
            String value = buffer.readString();
            techniques[i] = value.isEmpty() ? null : ShinsuTechnique.valueOf(value);
        }
        int length = buffer.readInt();
        String[] settings = new String[length];
        for (int i = 0; i < length; i++) {
            String value = buffer.readString();
            settings[i] = i >= size || techniques[i] == null ? null : value;
        }
        return new ChangeEquipsMessage(techniques, settings);
    };
    private static final BiConsumer<ChangeEquipsMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique[] equipped;
    private final String[] settings;

    public ChangeEquipsMessage(ShinsuTechnique[] equipped, String[] settings) {
        this.equipped = equipped;
        this.settings = settings;
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
                tar.setSettings(settings);
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.equipped = equipped;
                ClientReference.settings = settings;
            });
            context.setPacketHandled(true);
        }
    }

}

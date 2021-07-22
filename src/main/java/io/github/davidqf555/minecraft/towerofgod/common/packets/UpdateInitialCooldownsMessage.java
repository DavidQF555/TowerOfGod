package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateInitialCooldownsMessage {

    private static final BiConsumer<UpdateInitialCooldownsMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.technique.name());
        buffer.writeInt(message.cooldown);
    };
    private static final Function<PacketBuffer, UpdateInitialCooldownsMessage> DECODER = buffer -> new UpdateInitialCooldownsMessage(ShinsuTechnique.valueOf(buffer.readString()), buffer.readInt());
    private static final BiConsumer<UpdateInitialCooldownsMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique technique;
    private final int cooldown;

    public UpdateInitialCooldownsMessage(ShinsuTechnique technique, int cooldown) {
        this.technique = technique;
        this.cooldown = cooldown;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateInitialCooldownsMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.initialCooldowns.put(technique, cooldown);
            });
            context.setPacketHandled(true);
        }
    }

}

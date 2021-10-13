package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateInitialCooldownsPacket {

    private static final BiConsumer<UpdateInitialCooldownsPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.technique.name());
        buffer.writeInt(message.cooldown);
    };
    private static final Function<PacketBuffer, UpdateInitialCooldownsPacket> DECODER = buffer -> new UpdateInitialCooldownsPacket(ShinsuTechnique.valueOf(buffer.readString()), buffer.readInt());
    private static final BiConsumer<UpdateInitialCooldownsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique technique;
    private final int cooldown;

    public UpdateInitialCooldownsPacket(ShinsuTechnique technique, int cooldown) {
        this.technique = technique;
        this.cooldown = cooldown;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateInitialCooldownsPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.INITIAL_COOLDOWNS.put(technique, cooldown);
            });
            context.setPacketHandled(true);
        }
    }

}

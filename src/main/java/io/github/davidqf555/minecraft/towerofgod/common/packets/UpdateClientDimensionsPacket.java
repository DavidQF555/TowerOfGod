package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientDimensionsPacket {

    private static final BiConsumer<UpdateClientDimensionsPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.key.getLocation().toString());
    };
    private static final Function<PacketBuffer, UpdateClientDimensionsPacket> DECODER = buffer -> new UpdateClientDimensionsPacket(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buffer.readString())));
    private static final BiConsumer<UpdateClientDimensionsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final RegistryKey<World> key;

    public UpdateClientDimensionsPacket(RegistryKey<World> key) {
        this.key = key;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientDimensionsPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                Minecraft.getInstance().player.connection.getDimensionKeys().add(key);
            });
            context.setPacketHandled(true);
        }
    }

}
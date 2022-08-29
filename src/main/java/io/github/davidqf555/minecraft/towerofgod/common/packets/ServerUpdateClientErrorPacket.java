package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateClientErrorPacket {

    private static final BiConsumer<ServerUpdateClientErrorPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.errors.size());
        for (Map.Entry<ShinsuTechnique, Component> error : message.errors.entrySet()) {
            buffer.writeResourceLocation(error.getKey().getRegistryName());
            buffer.writeComponent(error.getValue());
        }
    };
    private static final Function<FriendlyByteBuf, ServerUpdateClientErrorPacket> DECODER = buffer -> {
        Map<ShinsuTechnique, Component> errors = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechnique technique = ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation());
            errors.put(technique, buffer.readComponent());
        }
        return new ServerUpdateClientErrorPacket(errors);
    };
    private static final BiConsumer<ServerUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, Component> errors;

    public ServerUpdateClientErrorPacket(Map<ShinsuTechnique, Component> errors) {
        this.errors = errors;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerUpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientReference.ERRORS.clear();
            ClientReference.ERRORS.putAll(errors);
        });
        context.setPacketHandled(true);
    }
}

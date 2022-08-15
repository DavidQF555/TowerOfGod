package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechnique;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerUpdateClientErrorPacket {

    private static final BiConsumer<ServerUpdateClientErrorPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.errors.size());
        for (Map.Entry<ShinsuTechnique, ITextComponent> error : message.errors.entrySet()) {
            buffer.writeString(error.getKey().name());
            buffer.writeTextComponent(error.getValue());
        }
    };
    private static final Function<PacketBuffer, ServerUpdateClientErrorPacket> DECODER = buffer -> {
        Map<ShinsuTechnique, ITextComponent> errors = new EnumMap<>(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
            errors.put(technique, buffer.readTextComponent());
        }
        return new ServerUpdateClientErrorPacket(errors);
    };
    private static final BiConsumer<ServerUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, ITextComponent> errors;

    public ServerUpdateClientErrorPacket(Map<ShinsuTechnique, ITextComponent> errors) {
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

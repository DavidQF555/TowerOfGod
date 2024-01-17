package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerOpenCombinationGUIPacket {

    private static final BiConsumer<ServerOpenCombinationGUIPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.usable.size());
        message.usable.forEach(technique -> buffer.writeResourceLocation(ShinsuTechniqueRegistry.getRegistry().getKey(technique)));
    };
    private static final Function<FriendlyByteBuf, ServerOpenCombinationGUIPacket> DECODER = buffer -> {
        Set<ShinsuTechnique> usable = new HashSet<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            usable.add(ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation()));
        }
        return new ServerOpenCombinationGUIPacket(usable);
    };

    private final Set<ShinsuTechnique> usable;

    public ServerOpenCombinationGUIPacket(Set<ShinsuTechnique> usable) {
        this.usable = usable;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ServerOpenCombinationGUIPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ServerOpenCombinationGUIPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.openCombinationGUI(usable);
    }

}

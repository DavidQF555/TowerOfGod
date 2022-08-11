package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerOpenCombinationGUIPacket {

    private static final BiConsumer<ServerOpenCombinationGUIPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.unlocked.size());
        for (ShinsuTechnique technique : message.unlocked) {
            buffer.writeInt(technique.ordinal());
        }
    };
    private static final Function<PacketBuffer, ServerOpenCombinationGUIPacket> DECODER = buffer -> {
        Set<ShinsuTechnique> unlocked = EnumSet.noneOf(ShinsuTechnique.class);
        ShinsuTechnique[] all = ShinsuTechnique.values();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            unlocked.add(all[buffer.readInt()]);
        }
        return new ServerOpenCombinationGUIPacket(unlocked);
    };
    private static final BiConsumer<ServerOpenCombinationGUIPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Set<ShinsuTechnique> unlocked;

    public ServerOpenCombinationGUIPacket(Set<ShinsuTechnique> unlocked) {
        this.unlocked = unlocked;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ServerOpenCombinationGUIPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.openCombinationGUI(unlocked));
        context.setPacketHandled(true);
    }
}

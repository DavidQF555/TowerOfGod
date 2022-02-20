package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.gui.GuideScreen;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OpenGuideScreenPacket {

    private static final BiConsumer<OpenGuideScreenPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.pages.length);
        for (ShinsuTechnique technique : message.pages) {
            buffer.writeString(technique.name());
        }
        buffer.writeInt(message.color);
    };
    private static final Function<PacketBuffer, OpenGuideScreenPacket> DECODER = buffer -> {
        int size = buffer.readInt();
        ShinsuTechnique[] pages = new ShinsuTechnique[size];
        for (int i = 0; i < size; i++) {
            pages[i] = ShinsuTechnique.valueOf(buffer.readString());
        }
        return new OpenGuideScreenPacket(pages, buffer.readInt());
    };
    private static final BiConsumer<OpenGuideScreenPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique[] pages;
    private final int color;

    public OpenGuideScreenPacket(ShinsuTechnique[] pages, int color) {
        this.pages = pages;
        this.color = color;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, OpenGuideScreenPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> Minecraft.getInstance().displayGuiScreen(new GuideScreen(pages, 221, 180, color)));
            context.setPacketHandled(true);
        }
    }

}

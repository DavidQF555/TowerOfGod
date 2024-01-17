package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class OpenGuideScreenPacket {

    private static final BiConsumer<OpenGuideScreenPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.pages.length);
        for (ShinsuTechnique technique : message.pages) {
            buffer.writeResourceLocation(technique.getId());
        }
        buffer.writeInt(message.color);
    };
    private static final Function<FriendlyByteBuf, OpenGuideScreenPacket> DECODER = buffer -> {
        int size = buffer.readInt();
        ShinsuTechnique[] pages = new ShinsuTechnique[size];
        for (int i = 0; i < size; i++) {
            pages[i] = ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation());
        }
        return new OpenGuideScreenPacket(pages, buffer.readInt());
    };
    private final ShinsuTechnique[] pages;
    private final int color;

    public OpenGuideScreenPacket(ShinsuTechnique[] pages, int color) {
        this.pages = pages;
        this.color = color;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(OpenGuideScreenPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(OpenGuideScreenPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.openGuideScreen(pages, color);
    }

}

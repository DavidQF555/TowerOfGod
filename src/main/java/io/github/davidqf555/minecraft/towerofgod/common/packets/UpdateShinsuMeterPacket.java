package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateShinsuMeterPacket {

    private static final BiConsumer<UpdateShinsuMeterPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.shinsu);
        buffer.writeInt(message.maxShinsu);
    };
    private static final Function<FriendlyByteBuf, UpdateShinsuMeterPacket> DECODER = buffer -> new UpdateShinsuMeterPacket(buffer.readInt(), buffer.readInt());

    private final int shinsu;
    private final int maxShinsu;

    public UpdateShinsuMeterPacket(int shinsu, int maxShinsu) {
        this.shinsu = shinsu;
        this.maxShinsu = maxShinsu;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(UpdateShinsuMeterPacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(UpdateShinsuMeterPacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
            ClientReference.SHINSU.setValue(shinsu);
            ClientReference.SHINSU.setMax(maxShinsu);
    }

}

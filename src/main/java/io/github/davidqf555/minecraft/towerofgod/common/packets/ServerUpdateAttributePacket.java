package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerUpdateAttributePacket {

    private static final BiConsumer<ServerUpdateAttributePacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.attribute == null);
        if (message.attribute != null) {
            buffer.writeResourceLocation(message.attribute.getId());
        }
    };
    private static final Function<FriendlyByteBuf, ServerUpdateAttributePacket> DECODER = buffer -> new ServerUpdateAttributePacket(buffer.readInt(), buffer.readBoolean() ? null : ShinsuAttributeRegistry.getRegistry().getValue(buffer.readResourceLocation()));

    private final int id;
    private final ShinsuAttribute attribute;

    public ServerUpdateAttributePacket(int id, @Nullable ShinsuAttribute attribute) {
        this.id = id;
        this.attribute = attribute;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.messageBuilder(ServerUpdateAttributePacket.class, index, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ENCODER)
                .decoder(DECODER)
                .consumerMainThread(ServerUpdateAttributePacket::handle)
                .add();
    }

    private void handle(CustomPayloadEvent.Context context) {
        ClientReference.handleUpdateAttributePacket(id, attribute);
    }

}

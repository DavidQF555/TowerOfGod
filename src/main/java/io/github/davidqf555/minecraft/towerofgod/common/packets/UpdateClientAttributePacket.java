package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientAttributePacket {

    private static final BiConsumer<UpdateClientAttributePacket, FriendlyByteBuf> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.attribute == null);
        if (message.attribute != null) {
            buffer.writeResourceLocation(message.attribute.getRegistryName());
        }
    };
    private static final Function<FriendlyByteBuf, UpdateClientAttributePacket> DECODER = buffer -> new UpdateClientAttributePacket(buffer.readBoolean() ? null : ShinsuAttributeRegistry.getRegistry().getValue(buffer.readResourceLocation()));
    private static final BiConsumer<UpdateClientAttributePacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuAttribute attribute;

    public UpdateClientAttributePacket(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientAttributePacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.attribute = attribute);
        context.setPacketHandled(true);
    }
}

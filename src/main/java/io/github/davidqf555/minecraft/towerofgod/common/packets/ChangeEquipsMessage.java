package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeEquipsMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "change_equips_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ChangeEquipsMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.length);
        for (ShinsuTechnique technique : message.equipped) {
            buffer.writeString(technique == null ? "" : technique.getName());
        }
    };
    private static final Function<PacketBuffer, ChangeEquipsMessage> DECODER = buffer -> {
        ShinsuTechnique[] techniques = new ShinsuTechnique[4];
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            techniques[i] = ShinsuTechnique.get(buffer.readString());
        }
        return new ChangeEquipsMessage(techniques);
    };
    private static final BiConsumer<ChangeEquipsMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique[] equipped;

    public ChangeEquipsMessage(ShinsuTechnique[] equipped) {
        this.equipped = equipped;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, ChangeEquipsMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IPlayerShinsuEquips tar = IPlayerShinsuEquips.get(player);
                tar.setEquipped(equipped);
                UpdateClientEquippedMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientEquippedMessage(tar.getEquipped()));
            });
            context.setPacketHandled(true);
        }
    }

}

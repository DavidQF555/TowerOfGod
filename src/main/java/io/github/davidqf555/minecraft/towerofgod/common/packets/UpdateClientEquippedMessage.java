package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuSkillWheelGui;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientEquippedMessage {

    private static final BiConsumer<UpdateClientEquippedMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.length);
        for (ShinsuTechnique technique : message.equipped) {
            buffer.writeString(technique == null ? "" : technique.name());
        }
    };
    private static final Function<PacketBuffer, UpdateClientEquippedMessage> DECODER = buffer -> {
        int length = buffer.readInt();
        ShinsuTechnique[] equipped = new ShinsuTechnique[length];
        for (int i = 0; i < length; i++) {
            try {
                equipped[i] = ShinsuTechnique.valueOf(buffer.readString());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new UpdateClientEquippedMessage(equipped);
    };
    private static final BiConsumer<UpdateClientEquippedMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final ShinsuTechnique[] equipped;

    public UpdateClientEquippedMessage(ShinsuTechnique[] equipped) {
        this.equipped = equipped;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientEquippedMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IPlayerShinsuEquips equips = IPlayerShinsuEquips.get(player);
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientEquippedMessage(equips.getEquipped()));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ShinsuSkillWheelGui.equipped = equipped);
            context.setPacketHandled(true);
        }
    }
}

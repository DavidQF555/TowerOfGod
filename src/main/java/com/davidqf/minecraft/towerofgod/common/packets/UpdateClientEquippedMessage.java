package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuSkillWheelGui;
import com.davidqf.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
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

public class UpdateClientEquippedMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "update_equipped_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<UpdateClientEquippedMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.equipped.length);
        for (ShinsuTechnique technique : message.equipped) {
            buffer.writeString(technique == null ? "" : technique.getName());
        }
    };
    private static final Function<PacketBuffer, UpdateClientEquippedMessage> DECODER = buffer -> {
        int length = buffer.readInt();
        ShinsuTechnique[] equipped = new ShinsuTechnique[length];
        for (int i = 0; i < length; i++) {
            equipped[i] = ShinsuTechnique.get(buffer.readString());
        }
        return new UpdateClientEquippedMessage(equipped);
    };
    private static final BiConsumer<UpdateClientEquippedMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, UpdateClientEquippedMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private final ShinsuTechnique[] equipped;

    public UpdateClientEquippedMessage() {
        this(new ShinsuTechnique[0]);
    }

    public UpdateClientEquippedMessage(ShinsuTechnique[] equipped) {
        this.equipped = equipped;
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IPlayerShinsuEquips equips = IPlayerShinsuEquips.get(player);
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientEquippedMessage(equips.getEquipped()));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ShinsuSkillWheelGui.equipped = equipped);
            context.setPacketHandled(true);
        }
    }
}

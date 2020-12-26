package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.util.IPlayerShinsuEquips;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerEquipsSyncMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "player_equips_sync_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<PlayerEquipsSyncMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeCompoundTag(message.equips);
    };
    private static final Function<PacketBuffer, PlayerEquipsSyncMessage> DECODER = buffer -> {
        IPlayerShinsuEquips equips = new IPlayerShinsuEquips.PlayerShinsuEquips();
        equips.deserialize(buffer.readCompoundTag());
        return new PlayerEquipsSyncMessage(equips);
    };
    private static final BiConsumer<PlayerEquipsSyncMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final CompoundNBT equips;

    public PlayerEquipsSyncMessage(IPlayerShinsuEquips equips) {
        this.equips = equips.serialize();
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, PlayerEquipsSyncMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                IPlayerShinsuEquips tar = IPlayerShinsuEquips.get(player);
                tar.deserialize(equips);
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                IPlayerShinsuEquips tar = IPlayerShinsuEquips.get(Minecraft.getInstance().player);
                tar.deserialize(equips);
            });
            context.setPacketHandled(true);
        }
    }

}

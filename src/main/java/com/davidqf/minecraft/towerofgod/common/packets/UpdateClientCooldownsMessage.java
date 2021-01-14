package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuSkillWheelGui;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientCooldownsMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "update_cooldowns_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<UpdateClientCooldownsMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.cooldowns.size());
        for (ShinsuTechnique technique : message.cooldowns.keySet()) {
            buffer.writeString(technique.getName());
            buffer.writeInt(message.cooldowns.get(technique));
        }
    };
    private static final Function<PacketBuffer, UpdateClientCooldownsMessage> DECODER = buffer -> {
        int size = buffer.readInt();
        Map<ShinsuTechnique, Integer> cooldowns = Maps.newEnumMap(ShinsuTechnique.class);
        for (int i = 0; i < size; i++) {
            cooldowns.put(ShinsuTechnique.get(buffer.readString()), buffer.readInt());
        }
        return new UpdateClientCooldownsMessage(cooldowns);
    };
    private static final BiConsumer<UpdateClientCooldownsMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, UpdateClientCooldownsMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private final Map<ShinsuTechnique, Integer> cooldowns;

    public UpdateClientCooldownsMessage() {
        this(Maps.newEnumMap(ShinsuTechnique.class));
    }

    public UpdateClientCooldownsMessage(Map<ShinsuTechnique, Integer> cooldowns) {
        this.cooldowns = cooldowns;
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                IShinsuStats stats = IShinsuStats.get(player);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    cooldowns.put(technique, stats.getCooldown(technique));
                }
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientCooldownsMessage(cooldowns));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ShinsuSkillWheelGui.cooldowns = cooldowns;
            });
            context.setPacketHandled(true);
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.google.common.collect.Maps;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientCooldownsPacket {

    private static final BiConsumer<UpdateClientCooldownsPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.cooldowns.size());
        for (ShinsuTechnique technique : message.cooldowns.keySet()) {
            buffer.writeString(technique.name());
            buffer.writeInt(message.cooldowns.get(technique));
        }
    };
    private static final Function<PacketBuffer, UpdateClientCooldownsPacket> DECODER = buffer -> {
        int size = buffer.readInt();
        Map<ShinsuTechnique, Integer> cooldowns = Maps.newEnumMap(ShinsuTechnique.class);
        for (int i = 0; i < size; i++) {
            cooldowns.put(ShinsuTechnique.valueOf(buffer.readString()), buffer.readInt());
        }
        return new UpdateClientCooldownsPacket(cooldowns);
    };
    private static final BiConsumer<UpdateClientCooldownsPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, Integer> cooldowns;

    public UpdateClientCooldownsPacket() {
        this(Maps.newEnumMap(ShinsuTechnique.class));
    }

    public UpdateClientCooldownsPacket(Map<ShinsuTechnique, Integer> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientCooldownsPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                ShinsuStats stats = ShinsuStats.get(player);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    cooldowns.put(technique, stats.getCooldown(technique));
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientCooldownsPacket(cooldowns));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.cooldowns = cooldowns);
            context.setPacketHandled(true);
        }
    }
}

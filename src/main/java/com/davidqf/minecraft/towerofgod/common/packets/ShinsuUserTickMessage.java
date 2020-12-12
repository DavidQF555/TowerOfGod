package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuUserTickMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_tick_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ShinsuUserTickMessage, PacketBuffer> ENCODER = (message, buffer) -> {

    };
    private static final Function<PacketBuffer, ShinsuUserTickMessage> DECODER = buffer -> new ShinsuUserTickMessage();

    private static final BiConsumer<ShinsuUserTickMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        context.get().enqueueWork(() -> message.handle(context.get()));
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, ShinsuUserTickMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
                ServerWorld world = player.getServerWorld();
                IShinsuStats stats = IShinsuStats.get(player);
                List<ShinsuTechnique> techniques = stats.getTechniques();
                for (int i = techniques.size() - 1; i >= 0; i--) {
                    ShinsuTechnique attack = techniques.get(i);
                    attack.tick(world);
                    if (attack.ticksLeft() <= 0) {
                        attack.onEnd(world);
                        techniques.remove(i);
                    }
                }
                Map<ShinsuTechniques, Integer> cooldowns = stats.getCooldowns();
                List<ShinsuTechniques> keys = new ArrayList<>(cooldowns.keySet());
                for (ShinsuTechniques key : keys) {
                    cooldowns.put(key, Math.max(0, cooldowns.get(key) - 1));
                }
                ShinsuStatsSyncMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ShinsuStatsSyncMessage(IShinsuStats.get(player)));
                context.setPacketHandled(true);
            }
    }
}

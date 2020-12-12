package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancement;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuAdvancementCriteria;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuCriteriaCompletionMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_criteria_completion_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ShinsuCriteriaCompletionMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.advancement.getName().getKey());
    };
    private static final Function<PacketBuffer, ShinsuCriteriaCompletionMessage> DECODER = buffer -> new ShinsuCriteriaCompletionMessage(ShinsuAdvancement.get(buffer.readString()));

    private static final BiConsumer<ShinsuCriteriaCompletionMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        context.get().enqueueWork(() -> message.handle(context.get()));
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, ShinsuCriteriaCompletionMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private final ShinsuAdvancement advancement;

    public ShinsuCriteriaCompletionMessage(ShinsuAdvancement advancement) {
        this.advancement = advancement;
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ShinsuAdvancementCriteria criteria = advancement.getCriteria();
            ServerPlayerEntity player = context.getSender();
            criteria.onCompletion(player);
            context.setPacketHandled(true);
        }
    }
}

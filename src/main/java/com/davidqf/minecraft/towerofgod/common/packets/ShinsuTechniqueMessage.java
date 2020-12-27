package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShinsuTechniqueMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_technique_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ShinsuTechniqueMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.action.name());
        buffer.writeString(message.technique.getTechnique().getName().getKey());
        buffer.writeCompoundTag(message.technique.serializeNBT());
    };
    private static final Function<PacketBuffer, ShinsuTechniqueMessage> DECODER = buffer -> {
        Action action = Action.get(buffer.readString());
        ShinsuTechniqueInstance technique = ShinsuTechnique.get(buffer.readString()).getBuilder().emptyBuild();
        technique.deserializeNBT(buffer.readCompoundTag());
        return new ShinsuTechniqueMessage(action, technique);
    };

    private static final BiConsumer<ShinsuTechniqueMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        INSTANCE.registerMessage(index, ShinsuTechniqueMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private final Action action;
    private final ShinsuTechniqueInstance technique;

    public ShinsuTechniqueMessage(Action action, ShinsuTechniqueInstance technique) {
        this.action = action;
        this.technique = technique;
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerWorld world = context.getSender().getServerWorld();
            context.enqueueWork(() -> {
                switch (action) {
                    case END:
                        technique.onEnd(world);
                        break;
                    case TICK:
                        technique.tick(world);
                        break;
                    case USE:
                        technique.onUse(world);
                }
            });
            context.setPacketHandled(true);
        }
    }

    public enum Action {

        USE(),
        TICK(),
        END();

        public static Action get(String name) {
            for (Action action : values()) {
                if (action.name().equals(name)) {
                    return action;
                }
            }
            return null;
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import com.google.common.collect.Maps;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuSkillWheelGui;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientCanCastMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "update_can_cast_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<UpdateClientCanCastMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeBoolean(message.target != null);
        if (message.target != null) {
            buffer.writeUniqueId(message.target);
        }
        buffer.writeInt(message.canCast.size());
        for (ShinsuTechnique technique : message.canCast.keySet()) {
            buffer.writeString(technique.getName());
            buffer.writeBoolean(message.canCast.get(technique));
        }
    };
    private static final Function<PacketBuffer, UpdateClientCanCastMessage> DECODER = buffer -> {
        UUID target = null;
        if (buffer.readBoolean()) {
            target = buffer.readUniqueId();
        }
        Map<ShinsuTechnique, Boolean> canCast = Maps.newEnumMap(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            canCast.put(ShinsuTechnique.get(buffer.readString()), buffer.readBoolean());
        }
        return new UpdateClientCanCastMessage(target, canCast);
    };
    private static final BiConsumer<UpdateClientCanCastMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final UUID target;
    private final Map<ShinsuTechnique, Boolean> canCast;

    public UpdateClientCanCastMessage(@Nullable UUID target) {
        this(target, Maps.newEnumMap(ShinsuTechnique.class));
    }

    public UpdateClientCanCastMessage(@Nullable UUID target, Map<ShinsuTechnique, Boolean> canCast) {
        this.target = target;
        this.canCast = canCast;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, UpdateClientCanCastMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                Entity target = this.target == null ? null : player.getServerWorld().getEntityByUuid(this.target);
                IShinsuStats stats = IShinsuStats.get(player);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    canCast.put(technique, technique.getBuilder().canCast(technique, player, stats.getTechniqueLevel(technique), target, player.getLookVec()));
                }
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientCanCastMessage(this.target, canCast));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ShinsuSkillWheelGui.canCast = canCast;
            });
            context.setPacketHandled(true);
        }
    }
}

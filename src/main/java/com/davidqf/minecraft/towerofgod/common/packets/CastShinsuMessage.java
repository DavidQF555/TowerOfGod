package com.davidqf.minecraft.towerofgod.common.packets;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuMessage {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "cast_shinsu_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<CastShinsuMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeUniqueId(message.user);
        buffer.writeString(message.technique.getName().getKey());
        buffer.writeBoolean(message.target == null);
        if(message.target != null) {
            buffer.writeUniqueId(message.target);
        }
        buffer.writeBoolean(message.dir == null);
        if(message.dir != null) {
            buffer.writeBlockPos(new BlockPos(message.dir.x, message.dir.y, message.dir.z));
        }
        };
    private static final Function<PacketBuffer, CastShinsuMessage> DECODER = buffer -> {
        UUID user = buffer.readUniqueId();
        ShinsuTechniques technique = ShinsuTechniques.get(buffer.readString());
        UUID target = null;
        if(!buffer.readBoolean()){
            target = buffer.readUniqueId();
        }
        Vector3d dir = null;
        if(!buffer.readBoolean()){
            BlockPos pos = buffer.readBlockPos();
            dir = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        }
        return new CastShinsuMessage(user, technique, target, dir);
    };
    private static final BiConsumer<CastShinsuMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        message.handle(context.get());
    };

    private final UUID user;
    private final ShinsuTechniques technique;
    private final UUID target;
    private final Vector3d dir;

    public CastShinsuMessage(UUID user, ShinsuTechniques technique, @Nullable UUID target, @Nullable Vector3d dir) {
        this.user = user;
        this.technique = technique;
        this.target = target;
        this.dir = dir;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, CastShinsuMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection direction = context.getDirection();
        if (direction == NetworkDirection.PLAY_TO_SERVER) {
            ServerWorld world = context.getSender().getServerWorld();
            Entity user = world.getEntityByUuid(this.user);
            if(user instanceof LivingEntity) {
                IShinsuStats stats = IShinsuStats.get(user);
                stats.cast((LivingEntity) user, technique, target == null ? null : world.getEntityByUuid(target), dir);
            }
            context.setPacketHandled(true);
        }
    }

}

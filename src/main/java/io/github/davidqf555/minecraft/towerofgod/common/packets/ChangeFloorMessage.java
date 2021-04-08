package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeFloorMessage {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, "change_floor_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static final BiConsumer<ChangeFloorMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.teleporter.getX());
        buffer.writeInt(message.teleporter.getY());
        buffer.writeInt(message.teleporter.getZ());
        buffer.writeInt(message.level);
        buffer.writeInt(message.direction.getIndex());
    };
    private static final Function<PacketBuffer, ChangeFloorMessage> DECODER = buffer -> {
        BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        return new ChangeFloorMessage(buffer.readInt(), pos, Direction.byIndex(buffer.readInt()));
    };
    private static final BiConsumer<ChangeFloorMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int level;
    private final BlockPos teleporter;
    private final Direction direction;

    public ChangeFloorMessage(int level, BlockPos teleporter, Direction direction) {
        this.level = level;
        this.teleporter = teleporter;
        this.direction = direction;
    }

    public static void register(int index) {
        INSTANCE.registerMessage(index, ChangeFloorMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                if (IShinsuStats.get(player).getLevel() >= level) {
                    FloorDimensionsHelper.sendPlayerToFloor(player, teleporter, direction, level);
                }
            });
            context.setPacketHandled(true);
        }
    }

}

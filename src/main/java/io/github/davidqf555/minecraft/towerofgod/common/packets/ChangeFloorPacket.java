package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangeFloorPacket {

    private static final BiConsumer<ChangeFloorPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.teleporter.getX());
        buffer.writeInt(message.teleporter.getY());
        buffer.writeInt(message.teleporter.getZ());
        buffer.writeInt(message.level);
        buffer.writeInt(message.direction.getIndex());
    };
    private static final Function<PacketBuffer, ChangeFloorPacket> DECODER = buffer -> {
        BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        return new ChangeFloorPacket(buffer.readInt(), pos, Direction.byIndex(buffer.readInt()));
    };
    private static final BiConsumer<ChangeFloorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int level;
    private final BlockPos teleporter;
    private final Direction direction;

    public ChangeFloorPacket(int level, BlockPos teleporter, Direction direction) {
        this.level = level;
        this.teleporter = teleporter;
        this.direction = direction;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ChangeFloorPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                if (ShinsuStats.get(player).getLevel() >= level) {
                    FloorDimensionsHelper.sendPlayerToFloor(player, teleporter, direction, level);
                }
            });
            context.setPacketHandled(true);
        }
    }

}

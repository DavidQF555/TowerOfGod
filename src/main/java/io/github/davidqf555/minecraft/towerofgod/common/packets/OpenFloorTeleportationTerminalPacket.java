package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OpenFloorTeleportationTerminalPacket {

    private static final BiConsumer<OpenFloorTeleportationTerminalPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.teleporter.getX());
        buffer.writeInt(message.teleporter.getY());
        buffer.writeInt(message.teleporter.getZ());
        buffer.writeInt(message.level);
        buffer.writeInt(message.direction.get3DDataValue());
    };
    private static final Function<PacketBuffer, OpenFloorTeleportationTerminalPacket> DECODER = buffer -> {
        BlockPos teleporter = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        return new OpenFloorTeleportationTerminalPacket(buffer.readInt(), teleporter, Direction.from3DDataValue(buffer.readInt()));
    };
    private static final BiConsumer<OpenFloorTeleportationTerminalPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int level;
    private final BlockPos teleporter;
    private final Direction direction;

    public OpenFloorTeleportationTerminalPacket(int level, BlockPos teleporter, Direction direction) {
        this.level = level;
        this.teleporter = teleporter;
        this.direction = direction;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, OpenFloorTeleportationTerminalPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientReference.openFloorTeleportationTerminalScreen(level, teleporter, direction));
        context.setPacketHandled(true);
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.gui.FloorTeleportationTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OpenFloorTeleportationTerminalMessage {

    private static final BiConsumer<OpenFloorTeleportationTerminalMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.teleporter.getX());
        buffer.writeInt(message.teleporter.getY());
        buffer.writeInt(message.teleporter.getZ());
        buffer.writeInt(message.level);
        buffer.writeInt(message.direction.getIndex());
    };
    private static final Function<PacketBuffer, OpenFloorTeleportationTerminalMessage> DECODER = buffer -> {
        BlockPos teleporter = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        return new OpenFloorTeleportationTerminalMessage(buffer.readInt(), teleporter, Direction.byIndex(buffer.readInt()));
    };
    private static final BiConsumer<OpenFloorTeleportationTerminalMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final int level;
    private final BlockPos teleporter;
    private final Direction direction;

    public OpenFloorTeleportationTerminalMessage(int level, BlockPos teleporter, Direction direction) {
        this.level = level;
        this.teleporter = teleporter;
        this.direction = direction;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, OpenFloorTeleportationTerminalMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> Minecraft.getInstance().displayGuiScreen(new FloorTeleportationTerminalScreen(level, teleporter, direction)));
            context.setPacketHandled(true);
        }
    }
}

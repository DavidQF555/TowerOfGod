package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.world.FloorDimensionsHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class FloorCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("floor")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                        .executes(context -> execute(context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "level")))));

    }

    private static int execute(ServerPlayerEntity player, int floor) {
        FloorDimensionsHelper.forceSendPlayerToFloor(player, floor, player.getPositionVec());
        return 0;
    }
}

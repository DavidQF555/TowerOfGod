package com.davidqf.minecraft.towerofgod.common.util;

import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.packets.UpdateClientKnownMessage;
import com.davidqf.minecraft.towerofgod.common.packets.UpdateStatsMetersMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StatsCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("stats")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(new TypeSuggestionProvider())
                                .executes(context -> execute(EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "type")))
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(context -> execute(EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "type"), DoubleArgumentType.getDouble(context, "amount")))))));
    }

    private static int execute(Collection<ServerPlayerEntity> players, String type) {
        return execute(players, type, 1);
    }

    private static int execute(Collection<ServerPlayerEntity> players, String type, double amount) {
        if (amount != 0) {
            int fail = 0;
            for (ServerPlayerEntity player : players) {
                IShinsuStats stats = IShinsuStats.get(player);
                switch (type.toLowerCase()) {
                    case "shinsu":
                        stats.addMaxShinsu((int) amount);
                        break;
                    case "baangs":
                        stats.addMaxBaangs((int) amount);
                        break;
                    case "resistance":
                        if (amount < 0) {
                            amount = -1 / amount;
                        }
                        stats.multiplyResistance(amount);
                        break;
                    case "tension":
                        if (amount < 0) {
                            amount = -1 / amount;
                        }
                        stats.multiplyTension(amount);
                        break;
                    default:
                        ShinsuTechnique technique = ShinsuTechnique.get(type.toLowerCase());
                        if (technique == null) {
                            fail++;
                        } else {
                            stats.addKnownTechnique(technique, (int) amount);
                            Map<ShinsuTechnique, Integer> known = Maps.newEnumMap(ShinsuTechnique.class);
                            for (ShinsuTechnique t : ShinsuTechnique.values()) {
                                known.put(t, stats.getTechniqueLevel(t));
                            }
                            UpdateClientKnownMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientKnownMessage(known));
                        }
                }
                UpdateStatsMetersMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
            }
            return players.size() - fail;
        }
        return 0;
    }

    private static class TypeSuggestionProvider implements SuggestionProvider<CommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
            builder.suggest("shinsu");
            builder.suggest("baangs");
            builder.suggest("resistance");
            builder.suggest("tension");
            for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
                builder.suggest(technique.getName());
            }
            return builder.buildFuture();
        }
    }
}

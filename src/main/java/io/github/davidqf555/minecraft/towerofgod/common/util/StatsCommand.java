package io.github.davidqf555.minecraft.towerofgod.common.util;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientKnownMessage;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateStatsMetersMessage;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
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
                    case "level":
                        stats.addLevel((int) amount);
                        break;
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
                        stats.multiplyBaseResistance(amount);
                        break;
                    case "tension":
                        if (amount < 0) {
                            amount = -1 / amount;
                        }
                        stats.multiplyBaseTension(amount);
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
                            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientKnownMessage(known));
                        }
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
            }
            return players.size() - fail;
        }
        return 0;
    }

    private static class TypeSuggestionProvider implements SuggestionProvider<CommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
            builder.suggest("level");
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

package io.github.davidqf555.minecraft.towerofgod.common.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientKnownMessage;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateStatsMetersMessage;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ShinsuCommand {

    private static final TranslationTextComponent ZERO = new TranslationTextComponent("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
    private static final String LEVEL = "commands." + TowerOfGod.MOD_ID + ".shinsu.level";
    private static final String SHINSU = "commands." + TowerOfGod.MOD_ID + ".shinsu.shinsu";
    private static final String BAANGS = "commands." + TowerOfGod.MOD_ID + ".shinsu.baangs";
    private static final String RESISTANCE = "commands." + TowerOfGod.MOD_ID + ".shinsu.resistance";
    private static final String TENSION = "commands." + TowerOfGod.MOD_ID + ".shinsu.tension";
    private static final String TECHNIQUE_SUCCESS = "commands." + TowerOfGod.MOD_ID + ".shinsu.technique_success";
    private static final String TECHNIQUE_FAIL = "commands." + TowerOfGod.MOD_ID + ".shinsu.technique_fail";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("shinsu")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(new TypeSuggestionProvider())
                                .executes(context -> execute(context.getSource(), EntityArgument.getEntities(context, "targets"), StringArgumentType.getString(context, "type")))
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(context -> execute(context.getSource(), EntityArgument.getEntities(context, "targets"), StringArgumentType.getString(context, "type"), DoubleArgumentType.getDouble(context, "amount")))))
                )
        );
    }

    private static int execute(CommandSource source, Collection<? extends Entity> entities, String type) {
        return execute(source, entities, type, 1);
    }

    private static int execute(CommandSource source, Collection<? extends Entity> entities, String type, double amount) {
        if (amount == 0) {
            source.sendErrorMessage(ZERO);
            return 0;
        } else {
            int fail = 0;
            for (Entity entity : entities) {
                if (entity instanceof IShinsuUser || entity instanceof ServerPlayerEntity) {
                    ShinsuStats stats = ShinsuStats.get(entity);
                    switch (type.toLowerCase()) {
                        case "level":
                            stats.addLevel((int) amount);
                            source.sendFeedback(new TranslationTextComponent(LEVEL, entity.getDisplayName(), (int) amount), true);
                            break;
                        case "shinsu":
                            stats.addMaxShinsu((int) amount);
                            if (entity instanceof ServerPlayerEntity) {
                                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
                            }
                            source.sendFeedback(new TranslationTextComponent(SHINSU, entity.getDisplayName(), (int) amount), true);
                            break;
                        case "baangs":
                            stats.addMaxBaangs((int) amount);
                            if (entity instanceof ServerPlayerEntity) {
                                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
                            }
                            source.sendFeedback(new TranslationTextComponent(BAANGS, entity.getDisplayName(), (int) amount), true);
                            break;
                        case "resistance":
                            if (amount < 0) {
                                amount = -1 / amount;
                            }
                            stats.multiplyBaseResistance(amount);
                            source.sendFeedback(new TranslationTextComponent(RESISTANCE, entity.getDisplayName(), amount), true);
                            break;
                        case "tension":
                            if (amount < 0) {
                                amount = -1 / amount;
                            }
                            stats.multiplyBaseTension(amount);
                            source.sendFeedback(new TranslationTextComponent(TENSION, entity.getDisplayName(), amount), true);
                            break;
                        default:
                            ShinsuTechnique technique;
                            try {
                                technique = ShinsuTechnique.valueOf(type.toUpperCase());
                            } catch (IllegalArgumentException exception) {
                                source.sendErrorMessage(new TranslationTextComponent(TECHNIQUE_FAIL, entity.getDisplayName(), type));
                                fail++;
                                break;
                            }
                            stats.addKnownTechnique(technique, (int) amount);
                            if (entity instanceof ServerPlayerEntity) {
                                Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
                                for (ShinsuTechnique t : ShinsuTechnique.values()) {
                                    known.put(t, stats.getTechniqueLevel(t));
                                }
                                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientKnownMessage(known));
                            }
                            source.sendFeedback(new TranslationTextComponent(TECHNIQUE_SUCCESS, entity.getDisplayName(), technique.getText(), (int) amount), true);
                    }
                } else {
                    fail++;
                }
            }
            return entities.size() - fail;
        }
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
                builder.suggest(technique.name().toLowerCase());
            }
            return builder.buildFuture();
        }
    }
}

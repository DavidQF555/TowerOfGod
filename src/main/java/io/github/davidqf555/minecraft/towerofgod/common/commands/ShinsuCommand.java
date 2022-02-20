package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;

public final class ShinsuCommand {

    private static final TranslationTextComponent ZERO = new TranslationTextComponent("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
    private static final String LEVEL = "commands." + TowerOfGod.MOD_ID + ".shinsu.level";
    private static final String SHINSU = "commands." + TowerOfGod.MOD_ID + ".shinsu.shinsu";
    private static final String BAANGS = "commands." + TowerOfGod.MOD_ID + ".shinsu.baangs";
    private static final String RESISTANCE = "commands." + TowerOfGod.MOD_ID + ".shinsu.resistance";
    private static final String TENSION = "commands." + TowerOfGod.MOD_ID + ".shinsu.tension";
    private static final String TECHNIQUE = "commands." + TowerOfGod.MOD_ID + ".shinsu.technique";

    private ShinsuCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("shinsu")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("level")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> changeLevel(context.getSource(), EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "value")))
                                )
                        )
                        .then(Commands.literal("shinsu")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> changeShinsu(context.getSource(), EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "value")))
                                )
                        )
                        .then(Commands.literal("baangs")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> changeBaangs(context.getSource(), EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "value")))
                                )
                        )
                        .then(Commands.literal("resistance")
                                .then(Commands.argument("factor", DoubleArgumentType.doubleArg())
                                        .executes(context -> changeResistance(context.getSource(), EntityArgument.getEntities(context, "targets"), DoubleArgumentType.getDouble(context, "factor")))
                                )
                        )
                        .then(Commands.literal("tension")
                                .then(Commands.argument("factor", DoubleArgumentType.doubleArg())
                                        .executes(context -> changeTension(context.getSource(), EntityArgument.getEntities(context, "targets"), DoubleArgumentType.getDouble(context, "factor")))
                                )
                        )
                        .then(Commands.literal("technique")
                                .then(Commands.argument("type", EnumArgument.enumArgument(ShinsuTechniqueType.class))
                                        .executes(context -> changeTechniqueTypeLevel(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("type", ShinsuTechniqueType.class)))
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .executes(context -> changeTechniqueTypeLevel(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("type", ShinsuTechniqueType.class), IntegerArgumentType.getInteger(context, "value")))
                                        )
                                )
                        )
                )
        );
    }

    private static int changeLevel(CommandSource source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).addLevel(change);
            source.sendFeedback(new TranslationTextComponent(LEVEL, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeShinsu(CommandSource source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            stats.addMaxShinsu(change);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
            }
            source.sendFeedback(new TranslationTextComponent(SHINSU, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeBaangs(CommandSource source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            stats.addMaxBaangs(change);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
            }
            source.sendFeedback(new TranslationTextComponent(BAANGS, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeResistance(CommandSource source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendErrorMessage(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseResistance(factor);
            source.sendFeedback(new TranslationTextComponent(RESISTANCE, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int changeTension(CommandSource source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendErrorMessage(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseTension(factor);
            source.sendFeedback(new TranslationTextComponent(TENSION, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int changeTechniqueTypeLevel(CommandSource source, Collection<? extends Entity> entities, ShinsuTechniqueType type) {
        return changeTechniqueTypeLevel(source, entities, type, 1);
    }

    private static int changeTechniqueTypeLevel(CommandSource source, Collection<? extends Entity> entities, ShinsuTechniqueType type, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            ShinsuTypeData d = stats.getData(type);
            d.setLevel(d.getLevel() + change);
            source.sendFeedback(new TranslationTextComponent(TECHNIQUE, entity.getDisplayName(), type.getText(), change), true);
        }
        return entities.size();
    }
}

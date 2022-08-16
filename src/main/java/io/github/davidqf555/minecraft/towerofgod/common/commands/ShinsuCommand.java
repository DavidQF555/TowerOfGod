package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientQualityPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;

public final class ShinsuCommand {

    private static final TranslationTextComponent ZERO = new TranslationTextComponent("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
    private static final String REMOVE_SHAPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_shape";
    private static final String REMOVE_QUALITY = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_quality";
    private static final String LEVEL = "commands." + TowerOfGod.MOD_ID + ".shinsu.level";
    private static final String SHINSU = "commands." + TowerOfGod.MOD_ID + ".shinsu.shinsu";
    private static final String BAANGS = "commands." + TowerOfGod.MOD_ID + ".shinsu.baangs";
    private static final String RESISTANCE = "commands." + TowerOfGod.MOD_ID + ".shinsu.resistance";
    private static final String TENSION = "commands." + TowerOfGod.MOD_ID + ".shinsu.tension";
    private static final String TECHNIQUE_TYPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.technique";
    private static final String QUALITY = "commands." + TowerOfGod.MOD_ID + ".shinsu.quality";
    private static final String SHAPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.shape";

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
                        .then(Commands.literal("quality")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", ResourceLocationArgument.resourceLocation())
                                                .suggests((context, builder) -> ISuggestionProvider.suggestIterable(ShinsuQualityRegistry.getRegistry().getKeys(), builder))
                                                .executes(context -> changeQuality(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getResourceLocation(context, "value")))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeQuality(context.getSource(), EntityArgument.getEntities(context, "targets")))
                                )
                        )
                        .then(Commands.literal("shape")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", ResourceLocationArgument.resourceLocation())
                                                .suggests((context, builder) -> ISuggestionProvider.suggestIterable(ShinsuShapeRegistry.getRegistry().getKeys(), builder))
                                                .executes(context -> changeShape(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getResourceLocation(context, "value")))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeShape(context.getSource(), EntityArgument.getEntities(context, "targets")))
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
            source.sendFeedback(new TranslationTextComponent(TECHNIQUE_TYPE, entity.getDisplayName(), type.getText(), change), true);
        }
        return entities.size();
    }

    private static int changeQuality(CommandSource source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuQuality quality = ShinsuQualityRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setQuality(quality);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientQualityPacket(quality));
            }
            source.sendFeedback(new TranslationTextComponent(QUALITY, entity.getDisplayName(), quality.getName()), true);
        }
        return entities.size();
    }

    private static int changeShape(CommandSource source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuShape shape = ShinsuShapeRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(shape);
            source.sendFeedback(new TranslationTextComponent(SHAPE, entity.getDisplayName(), shape.getName()), true);
        }
        return entities.size();
    }

    private static int removeQuality(CommandSource source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setQuality(null);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientQualityPacket(null));
            }
            source.sendFeedback(new TranslationTextComponent(REMOVE_QUALITY, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int removeShape(CommandSource source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(null);
            source.sendFeedback(new TranslationTextComponent(REMOVE_SHAPE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

}

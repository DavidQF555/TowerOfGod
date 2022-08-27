package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
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
    private static final String REMOVE_ATTRIBUTE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_attribute";
    private static final String LEVEL = "commands." + TowerOfGod.MOD_ID + ".shinsu.level";
    private static final String SHINSU = "commands." + TowerOfGod.MOD_ID + ".shinsu.shinsu";
    private static final String BAANGS = "commands." + TowerOfGod.MOD_ID + ".shinsu.baangs";
    private static final String RESISTANCE = "commands." + TowerOfGod.MOD_ID + ".shinsu.resistance";
    private static final String TENSION = "commands." + TowerOfGod.MOD_ID + ".shinsu.tension";
    private static final String TECHNIQUE_TYPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.technique";
    private static final String ATTRIBUTE = "commands." + TowerOfGod.MOD_ID + ".shinsu.attribute";
    private static final String SHAPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.shape";
    private static final String INSTANCES = "commands." + TowerOfGod.MOD_ID + ".shinsu.instances";
    private static final String INSTANCES_TITLE = "commands." + TowerOfGod.MOD_ID + ".shinsu.instances.entity";

    private ShinsuCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("shinsu")
                .requires(source -> source.hasPermission(2))
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
                        .then(Commands.literal("attribute")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> ISuggestionProvider.suggestResource(ShinsuAttributeRegistry.getRegistry().getKeys(), builder))
                                                .executes(context -> changeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getId(context, "value")))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets")))
                                )
                        )
                        .then(Commands.literal("shape")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> ISuggestionProvider.suggestResource(ShinsuShapeRegistry.getRegistry().getKeys(), builder))
                                                .executes(context -> changeShape(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceLocationArgument.getId(context, "value")))
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
                        .then(Commands.literal("instances")
                                .executes(context -> printInstances(context.getSource(), EntityArgument.getEntities(context, "targets")))
                        )
                )
        );
    }

    private static int changeLevel(CommandSource source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).addLevel(change);
            source.sendSuccess(new TranslationTextComponent(LEVEL, entity.getDisplayName(), change), true);
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
            source.sendSuccess(new TranslationTextComponent(SHINSU, entity.getDisplayName(), change), true);
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
            source.sendSuccess(new TranslationTextComponent(BAANGS, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeResistance(CommandSource source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseResistance(factor);
            source.sendSuccess(new TranslationTextComponent(RESISTANCE, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int changeTension(CommandSource source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseTension(factor);
            source.sendSuccess(new TranslationTextComponent(TENSION, entity.getDisplayName(), factor), true);
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
            source.sendSuccess(new TranslationTextComponent(TECHNIQUE_TYPE, entity.getDisplayName(), type.getText(), change), true);
        }
        return entities.size();
    }

    private static int changeAttribute(CommandSource source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuAttribute attribute = ShinsuAttributeRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setAttribute(attribute);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientAttributePacket(attribute));
            }
            source.sendSuccess(new TranslationTextComponent(ATTRIBUTE, entity.getDisplayName(), attribute.getName()), true);
        }
        return entities.size();
    }

    private static int changeShape(CommandSource source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuShape shape = ShinsuShapeRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(shape);
            source.sendSuccess(new TranslationTextComponent(SHAPE, entity.getDisplayName(), shape.getName()), true);
        }
        return entities.size();
    }

    private static int removeAttribute(CommandSource source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setAttribute(null);
            if (entity instanceof ServerPlayerEntity) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientAttributePacket(null));
            }
            source.sendSuccess(new TranslationTextComponent(REMOVE_ATTRIBUTE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int removeShape(CommandSource source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(null);
            source.sendSuccess(new TranslationTextComponent(REMOVE_SHAPE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int printInstances(CommandSource source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            source.sendSuccess(new TranslationTextComponent(INSTANCES_TITLE, entity.getDisplayName()), true);
            for (ShinsuTechniqueInstance inst : ShinsuStats.get(entity).getTechniques()) {
                source.sendSuccess(new TranslationTextComponent(INSTANCES, inst.getTechnique().getText(), (inst.getDuration() - inst.getTicks()) / 20.0), true);
            }
        }
        return entities.size();
    }

}

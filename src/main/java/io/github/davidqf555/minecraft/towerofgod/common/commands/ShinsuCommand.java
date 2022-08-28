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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;

public final class ShinsuCommand {

    private static final TranslatableComponent ZERO = new TranslatableComponent("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(ShinsuAttributeRegistry.getRegistry().getKeys(), builder))
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
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(ShinsuShapeRegistry.getRegistry().getKeys(), builder))
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

    private static int changeLevel(CommandSourceStack source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).addLevel(change);
            source.sendSuccess(new TranslatableComponent(LEVEL, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeShinsu(CommandSourceStack source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            stats.addMaxShinsu(change);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateShinsuMeterPacket(stats.getShinsu(), stats.getMaxShinsu()));
            }
            source.sendSuccess(new TranslatableComponent(SHINSU, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeBaangs(CommandSourceStack source, Collection<? extends Entity> entities, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            stats.addMaxBaangs(change);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateBaangsMeterPacket(stats.getBaangs(), stats.getMaxBaangs()));
            }
            source.sendSuccess(new TranslatableComponent(BAANGS, entity.getDisplayName(), change), true);
        }
        return entities.size();
    }

    private static int changeResistance(CommandSourceStack source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseResistance(factor);
            source.sendSuccess(new TranslatableComponent(RESISTANCE, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int changeTension(CommandSourceStack source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        if (factor < 0) {
            factor = -1 / factor;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).multiplyBaseTension(factor);
            source.sendSuccess(new TranslatableComponent(TENSION, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int changeTechniqueTypeLevel(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuTechniqueType type) {
        return changeTechniqueTypeLevel(source, entities, type, 1);
    }

    private static int changeTechniqueTypeLevel(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuTechniqueType type, int change) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            ShinsuTypeData d = stats.getData(type);
            d.setLevel(d.getLevel() + change);
            source.sendSuccess(new TranslatableComponent(TECHNIQUE_TYPE, entity.getDisplayName(), type.getText(), change), true);
        }
        return entities.size();
    }

    private static int changeAttribute(CommandSourceStack source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuAttribute attribute = ShinsuAttributeRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setAttribute(attribute);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateClientAttributePacket(attribute));
            }
            source.sendSuccess(new TranslatableComponent(ATTRIBUTE, entity.getDisplayName(), attribute.getName()), true);
        }
        return entities.size();
    }

    private static int changeShape(CommandSourceStack source, Collection<? extends Entity> entities, ResourceLocation loc) {
        ShinsuShape shape = ShinsuShapeRegistry.getRegistry().getValue(loc);
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(shape);
            source.sendSuccess(new TranslatableComponent(SHAPE, entity.getDisplayName(), shape.getName()), true);
        }
        return entities.size();
    }

    private static int removeAttribute(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setAttribute(null);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateClientAttributePacket(null));
            }
            source.sendSuccess(new TranslatableComponent(REMOVE_ATTRIBUTE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int removeShape(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setShape(null);
            source.sendSuccess(new TranslatableComponent(REMOVE_SHAPE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int printInstances(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            source.sendSuccess(new TranslatableComponent(INSTANCES_TITLE, entity.getDisplayName()), true);
            for (ShinsuTechniqueInstance inst : ShinsuStats.get(entity).getTechniques()) {
                source.sendSuccess(new TranslatableComponent(INSTANCES, inst.getTechnique().getText(), (inst.getDuration() - inst.getTicks()) / 20.0), true);
            }
        }
        return entities.size();
    }

}

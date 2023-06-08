package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public final class ShinsuCommand {

    private static final MutableComponent ZERO = Component.translatable("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
    private static final String REMOVE_SHAPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_shape";
    private static final String REMOVE_ATTRIBUTE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_attribute";
    private static final String SHINSU = "commands." + TowerOfGod.MOD_ID + ".shinsu.shinsu";
    private static final String RESISTANCE = "commands." + TowerOfGod.MOD_ID + ".shinsu.resistance";
    private static final String TENSION = "commands." + TowerOfGod.MOD_ID + ".shinsu.tension";
    private static final String UNLOCK = "commands." + TowerOfGod.MOD_ID + ".shinsu.unlock";
    private static final String LOCK = "commands." + TowerOfGod.MOD_ID + ".shinsu.lock";
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
                        .then(Commands.literal("shinsu")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                        .executes(context -> setMaxShinsu(context.getSource(), EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "value")))
                                )
                        )
                        .then(Commands.literal("resistance")
                                .then(Commands.argument("factor", DoubleArgumentType.doubleArg(0))
                                        .executes(context -> setResistance(context.getSource(), EntityArgument.getEntities(context, "targets"), DoubleArgumentType.getDouble(context, "factor")))
                                )
                        )
                        .then(Commands.literal("tension")
                                .then(Commands.argument("factor", DoubleArgumentType.doubleArg(0))
                                        .executes(context -> setTension(context.getSource(), EntityArgument.getEntities(context, "targets"), DoubleArgumentType.getDouble(context, "factor")))
                                )
                        )
                        .then(Commands.literal("technique")
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("technique", new ShinsuTechniqueArgumentType())
                                                .executes(context -> unlockTechnique(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("technique", ShinsuTechnique.class)))
                                        )
                                )
                                .then(Commands.literal("lock")
                                        .then(Commands.argument("technique", new ShinsuTechniqueArgumentType())
                                                .executes(context -> lockTechnique(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("technique", ShinsuTechnique.class)))
                                        )
                                )
                        )
                        .then(Commands.literal("attribute")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", new ShinsuAttributeArgumentType())
                                                .executes(context -> changeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("value", ShinsuAttribute.class)))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets")))
                                )
                        )
                        .then(Commands.literal("shape")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", new ShinsuShapeArgumentType())
                                                .executes(context -> changeShape(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("value", ShinsuShape.class)))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeShape(context.getSource(), EntityArgument.getEntities(context, "targets")))
                                )
                        )
                        .then(Commands.literal("instances")
                                .executes(context -> printInstances(context.getSource(), EntityArgument.getEntities(context, "targets")))
                        )
                )
        );
    }

    private static int setMaxShinsu(CommandSourceStack source, Collection<? extends Entity> entities, int shinsu) {
        for (Entity entity : entities) {
            ShinsuStats stats = ShinsuStats.get(entity);
            stats.setMaxShinsu(shinsu);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateShinsuMeterPacket(ShinsuStats.getShinsu(entity), stats.getMaxShinsu()));
            }
            source.sendSuccess(() -> Component.translatable(SHINSU, entity.getDisplayName(), shinsu), true);
        }
        return entities.size();
    }

    private static int setResistance(CommandSourceStack source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setResistance(factor);
            source.sendSuccess(() -> Component.translatable(RESISTANCE, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int setTension(CommandSourceStack source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setTension(factor);
            source.sendSuccess(() -> Component.translatable(TENSION, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int unlockTechnique(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuTechnique technique) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player && PlayerTechniqueData.get((Player) entity).unlock(technique)) {
                count++;
                source.sendSuccess(() -> Component.translatable(UNLOCK, entity.getDisplayName(), technique.getText()), true);
            }
        }
        return count;
    }

    private static int lockTechnique(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuTechnique technique) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player && PlayerTechniqueData.get((Player) entity).lock(technique)) {
                count++;
                source.sendSuccess(() -> Component.translatable(LOCK, entity.getDisplayName(), technique.getText()), true);
            }
        }
        return count;
    }

    private static int changeAttribute(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuAttribute attribute) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setAttribute(attribute);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ServerUpdateAttributePacket(entity.getId(), attribute));
            }
            source.sendSuccess(() -> Component.translatable(ATTRIBUTE, entity.getDisplayName(), attribute.getName()), true);
        }
        return entities.size();
    }

    private static int changeShape(CommandSourceStack source, Collection<? extends Entity> entities, ShinsuShape shape) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setShape(shape);
            source.sendSuccess(() -> Component.translatable(SHAPE, entity.getDisplayName(), shape.getName()), true);
        }
        return entities.size();
    }

    private static int removeAttribute(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setAttribute(null);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ServerUpdateAttributePacket(entity.getId(), null));
            }
            source.sendSuccess(() -> Component.translatable(REMOVE_ATTRIBUTE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int removeShape(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setShape(null);
            source.sendSuccess(() -> Component.translatable(REMOVE_SHAPE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int printInstances(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            source.sendSuccess(() -> Component.translatable(INSTANCES_TITLE, entity.getDisplayName()), true);
            for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(entity).getTechniques()) {
                source.sendSuccess(() -> Component.translatable(INSTANCES, inst.getTechnique().getText(), (inst.getDuration() - inst.getTicks()) / 20.0), true);
            }
        }
        return entities.size();
    }

}

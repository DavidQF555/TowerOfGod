package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.BaangsTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateAttributePacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ServerUpdateUnlockedPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateMaxBaangsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public final class ShinsuCommand {

    private static final TranslatableComponent ZERO = new TranslatableComponent("commands." + TowerOfGod.MOD_ID + ".shinsu.zero");
    private static final String REMOVE_SHAPE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_shape";
    private static final String REMOVE_ATTRIBUTE = "commands." + TowerOfGod.MOD_ID + ".shinsu.remove_attribute";
    private static final String BAANGS = "commands." + TowerOfGod.MOD_ID + ".shinsu.baangs";
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
                        .then(Commands.literal("baangs")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                        .executes(context -> setMaxBaangs(context.getSource(), EntityArgument.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "value")))
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
                                        .then(Commands.argument("technique", new ResourceKeyArgument<>(ConfiguredTechniqueTypeRegistry.REGISTRY))
                                                .executes(context -> unlockTechnique(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("technique", ResourceKey.class)))
                                        )
                                )
                                .then(Commands.literal("lock")
                                        .then(Commands.argument("technique", new ResourceKeyArgument<>(ConfiguredTechniqueTypeRegistry.REGISTRY))
                                                .executes(context -> lockTechnique(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("technique", ResourceKey.class)))
                                        )
                                )
                        )
                        .then(Commands.literal("attribute")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", new ResourceKeyArgument<>(ShinsuAttributeRegistry.REGISTRY))
                                                .executes(context -> changeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("value", ResourceKey.class)))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .executes(context -> removeAttribute(context.getSource(), EntityArgument.getEntities(context, "targets")))
                                )
                        )
                        .then(Commands.literal("shape")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", new ResourceKeyArgument<>(ShinsuShapeRegistry.REGISTRY))
                                                .executes(context -> changeShape(context.getSource(), EntityArgument.getEntities(context, "targets"), context.getArgument("value", ResourceKey.class)))
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

    private static int setMaxBaangs(CommandSourceStack source, Collection<? extends Entity> entities, int baangs) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                ShinsuTechniqueData<?> data = ShinsuTechniqueData.get((LivingEntity) entity);
                if (data instanceof BaangsTechniqueData<?> cap) {
                    cap.setMaxBaangs(baangs);
                    source.sendSuccess(new TranslatableComponent(BAANGS, entity.getDisplayName(), baangs), true);
                    count++;
                    if (entity instanceof ServerPlayer) {
                        TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new UpdateMaxBaangsPacket(baangs));
                    }
                }
            }
        }
        return count;
    }

    private static int setResistance(CommandSourceStack source, Collection<? extends Entity> entities, double factor) {
        if (factor == 0) {
            source.sendFailure(ZERO);
            return 0;
        }
        for (Entity entity : entities) {
            ShinsuStats.get(entity).setResistance(factor);
            source.sendSuccess(new TranslatableComponent(RESISTANCE, entity.getDisplayName(), factor), true);
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
            source.sendSuccess(new TranslatableComponent(TENSION, entity.getDisplayName(), factor), true);
        }
        return entities.size();
    }

    private static int unlockTechnique(CommandSourceStack source, Collection<? extends Entity> entities, ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> technique) {
        int count = 0;
        Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(source.getServer().registryAccess());
        ConfiguredShinsuTechniqueType<?, ?> val = registry.getOrThrow(technique);
        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer) {
                PlayerTechniqueData data = PlayerTechniqueData.get((Player) entity);
                if (data.unlock(technique)) {
                    count++;
                    source.sendSuccess(new TranslatableComponent(UNLOCK, entity.getDisplayName(), val.getConfig().getDisplay().name()), true);
                    TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new ServerUpdateUnlockedPacket(data.getUnlocked()));
                }
            }
        }
        return count;
    }

    private static int lockTechnique(CommandSourceStack source, Collection<? extends Entity> entities, ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> technique) {
        int count = 0;
        Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(source.getServer().registryAccess());
        ConfiguredShinsuTechniqueType<?, ?> val = registry.getOrThrow(technique);
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                PlayerTechniqueData data = PlayerTechniqueData.get((Player) entity);
                if (data.lock(technique)) {
                    count++;
                    source.sendSuccess(new TranslatableComponent(LOCK, entity.getDisplayName(), val.getConfig().getDisplay().name()), true);
                    TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new ServerUpdateUnlockedPacket(data.getUnlocked()));
                }
            }
        }
        return count;
    }

    private static int changeAttribute(CommandSourceStack source, Collection<? extends Entity> entities, ResourceKey<ShinsuAttribute> attribute) {
        ShinsuAttribute val = ShinsuAttributeRegistry.getRegistry().getValue(attribute.location());
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setAttribute(val);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ServerUpdateAttributePacket(entity.getId(), val));
            }
            source.sendSuccess(new TranslatableComponent(ATTRIBUTE, entity.getDisplayName(), val.getName()), true);
        }
        return entities.size();
    }

    private static int changeShape(CommandSourceStack source, Collection<? extends Entity> entities, ResourceKey<ShinsuShape> shape) {
        ShinsuShape val = ShinsuShapeRegistry.getRegistry().getValue(shape.location());
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setShape(val);
            source.sendSuccess(new TranslatableComponent(SHAPE, entity.getDisplayName(), val.getName()), true);
        }
        return entities.size();
    }

    private static int removeAttribute(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setAttribute(null);
            if (entity instanceof ServerPlayer) {
                TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ServerUpdateAttributePacket(entity.getId(), null));
            }
            source.sendSuccess(new TranslatableComponent(REMOVE_ATTRIBUTE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int removeShape(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            ShinsuQualityData.get(entity).setShape(null);
            source.sendSuccess(new TranslatableComponent(REMOVE_SHAPE, entity.getDisplayName()), true);
        }
        return entities.size();
    }

    private static int printInstances(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                source.sendSuccess(new TranslatableComponent(INSTANCES_TITLE, entity.getDisplayName()), true);
                for (ShinsuTechniqueInstance<?, ?> inst : ShinsuTechniqueData.get((LivingEntity) entity).getTechniques()) {
                    source.sendSuccess(new TranslatableComponent(INSTANCES, inst.getConfigured().getConfig().getDisplay().name(), inst.getConfigured().getConfig().getDuration().map(dur -> (dur - inst.getTicks()) / 20.0).orElse(Double.MAX_VALUE)), true);
                }
            }
        }
        return entities.size();
    }

}

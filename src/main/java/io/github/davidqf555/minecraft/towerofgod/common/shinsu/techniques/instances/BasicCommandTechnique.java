package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class BasicCommandTechnique<C extends ShinsuTechniqueConfig, S extends BasicCommandTechnique.Data> extends ShinsuTechniqueType<C, S> {

    public BasicCommandTechnique(Codec<C> config, Codec<S> data) {
        super(config, data);
    }

    public boolean isTarget(FlyingDevice device) {
        return true;
    }

    protected abstract DeviceCommand createCommand(LivingEntity user, C config, FlyingDevice entity, UUID id);

    protected abstract S createData(UUID id, List<UUID> devices);

    @Nullable
    @Override
    public S onUse(LivingEntity user, C config, @Nullable LivingEntity target) {
        UUID id = Mth.createInsecureUUID();
        List<UUID> devices = new ArrayList<>();
        for (Entity entity : ((ServerLevel) user.level).getAllEntities()) {
            if (entity instanceof FlyingDevice && user.getUUID().equals(((FlyingDevice) entity).getOwnerID()) && isTarget((FlyingDevice) entity)) {
                devices.add(entity.getUUID());
                ((FlyingDevice) entity).addCommand(createCommand(user, config, (FlyingDevice) entity, id));
            }
        }
        return createData(id, devices);
    }

    @Override
    public void onEnd(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        if (user.level instanceof ServerLevel) {
            UUID id = inst.getData().id;
            inst.getData().devices.stream()
                    .map(entity -> (FlyingDevice) ((ServerLevel) user.level).getEntity(entity))
                    .filter(Objects::nonNull)
                    .map(entity -> entity.goalSelector.getRunningGoals())
                    .forEach(stream ->
                            stream.map(WrappedGoal::getGoal)
                                    .filter(goal -> goal instanceof DeviceCommand)
                                    .map(goal -> (DeviceCommand) goal)
                                    .filter(command -> id.equals(command.getTechniqueID()))
                                    .forEach(DeviceCommand::remove)
                    );
        }
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        if (user.level instanceof ServerLevel) {
            UUID userID = user.getUUID();
            UUID id = inst.getData().id;
            List<UUID> devices = inst.getData().devices;
            for (int i = devices.size() - 1; i >= 0; i--) {
                Entity device = ((ServerLevel) user.level).getEntity(devices.get(i));
                if (!(device instanceof FlyingDevice) || !userID.equals(((FlyingDevice) device).getOwnerID()) || ((FlyingDevice) device).getCommands().stream().noneMatch(command -> id.equals(command.getTechniqueID()))) {
                    devices.remove(i);
                }
            }
            if (devices.isEmpty()) {
                inst.remove(user);
            }
        }
    }

    public static class Data extends ShinsuTechniqueInstanceData {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Util.UUID_CODEC.listOf().fieldOf("devices").forGetter(data -> data.devices)
        ).apply(inst, Data::new));
        public final List<UUID> devices;

        public Data(UUID id, List<UUID> devices) {
            super(id);
            this.devices = devices;
        }

    }

}

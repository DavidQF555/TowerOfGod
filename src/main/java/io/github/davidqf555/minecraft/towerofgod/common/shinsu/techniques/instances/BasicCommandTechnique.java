package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class BasicCommandTechnique extends ShinsuTechniqueInstance {

    private final List<UUID> devices;

    public BasicCommandTechnique(Entity user) {
        super(user);
        devices = new ArrayList<>();
        if (user != null) {
            for (Entity entity : ((ServerLevel) user.level()).getAllEntities()) {
                if (entity instanceof FlyingDevice && user.getUUID().equals(((FlyingDevice) entity).getOwnerID()) && isTarget((FlyingDevice) entity)) {
                    devices.add(entity.getUUID());
                }
            }
        }
    }

    public boolean isTarget(FlyingDevice device) {
        return true;
    }

    public List<UUID> getDevices() {
        return devices;
    }

    protected abstract DeviceCommand createCommand(FlyingDevice entity, ServerLevel world);

    @Override
    public void onUse(ServerLevel world) {
        devices.stream()
                .map(world::getEntity)
                .filter(Objects::nonNull)
                .forEach(entity -> ((FlyingDevice) entity).addCommand(createCommand((FlyingDevice) entity, world)));
        super.onUse(world);
    }

    @Override
    public void onEnd(ServerLevel world) {
        UUID id = getID();
        devices.stream()
                .map(entity -> (FlyingDevice) world.getEntity(entity))
                .filter(Objects::nonNull)
                .map(entity -> entity.goalSelector.getRunningGoals())
                .forEach(stream ->
                        stream.map(WrappedGoal::getGoal)
                                .filter(goal -> goal instanceof DeviceCommand)
                                .map(goal -> (DeviceCommand) goal)
                                .filter(command -> id.equals(command.getTechniqueID()))
                                .forEach(DeviceCommand::remove)
                );
        super.onEnd(world);
    }

    @Override
    public void tick(ServerLevel world) {
        Entity user = getUser(world);
        if (user != null) {
            UUID userID = user.getUUID();
            boolean removed = false;
            UUID id = getID();
            for (int i = devices.size() - 1; i >= 0; i--) {
                Entity device = world.getEntity(devices.get(i));
                if (!(device instanceof FlyingDevice) || !userID.equals(((FlyingDevice) device).getOwnerID()) || ((FlyingDevice) device).getCommands().stream().noneMatch(command -> id.equals(command.getTechniqueID()))) {
                    devices.remove(i);
                    removed = true;
                }
            }
            if (devices.isEmpty()) {
                remove(world);
            } else if (removed) {
                updateMeters(world);
            }
        }
        super.tick(world);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        ListTag devices = new ListTag();
        for (UUID id : this.devices) {
            devices.add(NbtUtils.createUUID(id));
        }
        nbt.put("Devices", devices);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Devices", Tag.TAG_LIST)) {
            for (Tag tag : nbt.getList("Devices", Tag.TAG_INT_ARRAY)) {
                devices.add(NbtUtils.loadUUID(tag));
            }
        }
    }
}

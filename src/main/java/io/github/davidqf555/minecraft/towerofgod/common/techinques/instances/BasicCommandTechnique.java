package io.github.davidqf555.minecraft.towerofgod.common.techinques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BasicCommandTechnique extends ShinsuTechniqueInstance {

    private final List<UUID> devices;

    public BasicCommandTechnique(LivingEntity user) {
        super(user);
        devices = user == null ? new ArrayList<>() : ((ServerWorld) user.world).getEntities()
                .filter(entity -> entity instanceof FlyingDevice)
                .map(entity -> (FlyingDevice) entity)
                .filter(entity -> user.getUniqueID().equals(entity.getOwnerID()))
                .filter(this::isTarget)
                .map(Entity::getUniqueID)
                .collect(Collectors.toList());
    }

    public boolean isTarget(FlyingDevice device) {
        return true;
    }

    public List<UUID> getDevices() {
        return devices;
    }

    protected abstract DeviceCommand createCommand(FlyingDevice entity, ServerWorld world);

    @Override
    public void onUse(ServerWorld world) {
        devices.stream()
                .map(world::getEntityByUuid)
                .filter(Objects::nonNull)
                .forEach(entity -> ((FlyingDevice) entity).addCommand(createCommand((FlyingDevice) entity, world)));
        super.onUse(world);
    }

    @Override
    public void onEnd(ServerWorld world) {
        UUID id = getID();
        devices.stream()
                .map(entity -> (FlyingDevice) world.getEntityByUuid(entity))
                .filter(Objects::nonNull)
                .map(entity -> entity.goalSelector.getRunningGoals())
                .forEach(stream ->
                        stream.map(PrioritizedGoal::getGoal)
                                .filter(goal -> goal instanceof DeviceCommand)
                                .map(goal -> (DeviceCommand) goal)
                                .filter(command -> id.equals(command.getTechniqueID()))
                                .forEach(DeviceCommand::remove)
                );
        super.onEnd(world);
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        Entity user = getUser(world);
        if (user != null) {
            UUID userID = user.getUniqueID();
            boolean removed = false;
            UUID id = getID();
            for (int i = devices.size() - 1; i >= 0; i--) {
                Entity device = world.getEntityByUuid(devices.get(i));
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
        super.periodicTick(world, period);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        ListNBT devices = new ListNBT();
        for (UUID id : this.devices) {
            devices.add(NBTUtil.func_240626_a_(id));
        }
        nbt.put("Devices", devices);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Devices", Constants.NBT.TAG_LIST)) {
            for (INBT tag : nbt.getList("Devices", Constants.NBT.TAG_INT_ARRAY)) {
                devices.add(NBTUtil.readUniqueId(tag));
            }
        }
    }
}

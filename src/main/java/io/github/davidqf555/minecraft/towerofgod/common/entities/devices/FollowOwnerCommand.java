package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.entity.Entity;

import java.util.EnumSet;
import java.util.UUID;

public class FollowOwnerCommand extends DeviceCommand {

    private static final double DISTANCE = 5;
    private Entity owner;

    public FollowOwnerCommand(FlyingDevice entity, UUID technique) {
        super(entity, technique, 1);
        owner = null;
        setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public static FollowOwnerCommand emptyBuild(FlyingDevice device) {
        return new FollowOwnerCommand(device, null);
    }

    @Override
    public boolean shouldExecute() {
        FlyingDevice entity = getEntity();
        owner = entity.getOwner();
        return owner != null && !owner.isSpectator() && entity.getDistanceSq(owner) >= DISTANCE * DISTANCE;
    }

    @Override
    public void resetTask() {
        getEntity().getNavigator().clearPath();
    }

    @Override
    public void tick() {
        getEntity().getNavigator().tryMoveToXYZ(owner.getPosX(), owner.getPosYEye(), owner.getPosZ(), 1);
    }

    @Override
    public CommandType getType() {
        return CommandType.FOLLOW_OWNER;
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.entity.Entity;

import java.util.EnumSet;
import java.util.UUID;

public class FollowOwnerCommand extends DeviceCommand {

    private static final double DISTANCE = 5;
    private static final int RECALCULATE_PERIOD = 10;
    private int recalculate;
    private Entity owner;

    public FollowOwnerCommand(FlyingDevice entity, UUID technique) {
        super(entity, technique, 1);
        recalculate = 0;
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
    public boolean shouldContinueExecuting() {
        return shouldExecute() && !getEntity().getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        getEntity().getNavigator().clearPath();
        recalculate = 0;
    }

    @Override
    public void tick() {
        if (--recalculate <= 0) {
            getEntity().getNavigator().tryMoveToEntityLiving(owner, 1);
            recalculate = RECALCULATE_PERIOD;
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.FOLLOW_OWNER;
    }
}

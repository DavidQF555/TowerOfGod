package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.world.entity.Entity;

import java.util.EnumSet;
import java.util.UUID;

public class FollowOwnerCommand extends DeviceCommand {

    private static final double DISTANCE = 5;
    private static final int RECALCULATE_PERIOD = 10;
    private final float speed;
    private int recalculate;
    private Entity owner;

    public FollowOwnerCommand(FlyingDevice entity, UUID technique, float speed) {
        super(entity, technique, 1);
        this.speed = speed;
        recalculate = 0;
        owner = null;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public static FollowOwnerCommand emptyBuild(FlyingDevice device) {
        return new FollowOwnerCommand(device, null, 1);
    }

    @Override
    public boolean canUse() {
        FlyingDevice entity = getEntity();
        owner = entity.getOwner();
        return owner != null && !owner.isSpectator() && entity.distanceToSqr(owner) >= DISTANCE * DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && !getEntity().getNavigation().isDone();
    }

    @Override
    public void stop() {
        getEntity().getNavigation().stop();
        recalculate = 0;
    }

    @Override
    public void tick() {
        if (--recalculate <= 0) {
            getEntity().getNavigation().moveTo(owner, speed);
            recalculate = RECALCULATE_PERIOD;
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.FOLLOW_OWNER;
    }
}

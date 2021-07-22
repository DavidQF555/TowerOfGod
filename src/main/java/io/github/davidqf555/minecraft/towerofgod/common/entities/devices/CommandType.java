package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import java.util.function.Function;

public enum CommandType {

    MOVE(true, MoveCommand::emptyBuild),
    LIGHTHOUSE_FLOW_CONTROL(false, LighthouseFlowControlCommand::emptyBuild),
    SCOUT(false, ScoutCommand::emptyBuild),
    FOLLOW_OWNER(true, FollowOwnerCommand::emptyBuild);

    private final boolean indefinite;
    private final Function<FlyingDevice, ? extends DeviceCommand> empty;

    CommandType(boolean indefinite, Function<FlyingDevice, ? extends DeviceCommand> empty) {
        this.indefinite = indefinite;
        this.empty = empty;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public DeviceCommand createEmpty(FlyingDevice device) {
        return empty.apply(device);
    }
}

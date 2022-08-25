package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import java.util.EnumSet;
import java.util.UUID;

public class MoveCommand extends DeviceCommand {

    private float speed;
    private Vector3d target;

    public MoveCommand(FlyingDevice entity, UUID technique, Vector3d pos, float speed) {
        super(entity, technique, 1);
        this.target = pos;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public static MoveCommand emptyBuild(FlyingDevice device) {
        return new MoveCommand(device, null, Vector3d.ZERO, 1);
    }

    @Override
    public void passiveTick() {
        if (getEntity().distanceToSqr(target) <= 1) {
            remove();
        }
        super.passiveTick();
    }

    @Override
    public void tick() {
        PathNavigator nav = getEntity().getNavigation();
        if (nav.isDone() && !nav.moveTo(target.x(), target.y(), target.z(), speed)) {
            remove();
        }
        super.tick();
    }

    @Override
    public void stop() {
        getEntity().getNavigation().stop();
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putFloat("Speed", speed);
        ListNBT target = new ListNBT();
        target.add(DoubleNBT.valueOf(this.target.x()));
        target.add(DoubleNBT.valueOf(this.target.y()));
        target.add(DoubleNBT.valueOf(this.target.z()));
        nbt.put("Target", target);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Constants.NBT.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
        if (nbt.contains("Target", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("Target", Constants.NBT.TAG_DOUBLE);
            target = new Vector3d(list.getDouble(0), list.getDouble(1), list.getDouble(2));
        }
    }
}

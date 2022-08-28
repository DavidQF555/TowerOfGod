package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class MoveCommand extends DeviceCommand {

    private float speed;
    private Vec3 target;

    public MoveCommand(FlyingDevice entity, UUID technique, Vec3 pos, float speed) {
        super(entity, technique, 1);
        this.target = pos;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public static MoveCommand emptyBuild(FlyingDevice device) {
        return new MoveCommand(device, null, Vec3.ZERO, 1);
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
        PathNavigation nav = getEntity().getNavigation();
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putFloat("Speed", speed);
        ListTag target = new ListTag();
        target.add(DoubleTag.valueOf(this.target.x()));
        target.add(DoubleTag.valueOf(this.target.y()));
        target.add(DoubleTag.valueOf(this.target.z()));
        nbt.put("Target", target);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Tag.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
        if (nbt.contains("Target", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("Target", Tag.TAG_DOUBLE);
            target = new Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
        }
    }
}

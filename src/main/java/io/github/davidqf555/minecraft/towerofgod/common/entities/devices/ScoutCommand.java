package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class ScoutCommand extends DeviceCommand {

    private float speed;
    private int range;
    private BlockPos center;

    public ScoutCommand(FlyingDevice entity, UUID technique, BlockPos center, float speed, int range, int duration) {
        super(entity, technique, duration);
        this.center = center;
        this.speed = speed;
        this.range = range;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public static ScoutCommand emptyBuild(FlyingDevice device) {
        return new ScoutCommand(device, null, null, 1, 0, 0);
    }

    @Override
    public void tick() {
        FlyingDevice device = getEntity();
        PathNavigator nav = device.getNavigation();
        if (nav.isDone()) {
            List<BlockPos> air = new ArrayList<>();
            List<BlockPos> border = new ArrayList<>();
            for (int y = -range; y <= range; y++) {
                double xRange = Math.sqrt(range * range - y * y);
                int xRounded = (int) xRange;
                for (int x = -xRounded; x <= xRounded; x++) {
                    int zRounded = (int) Math.sqrt(xRange * xRange - x * x);
                    for (int z = -zRounded; z <= zRounded; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (device.level.isEmptyBlock(pos)) {
                            air.add(pos);
                            for (Direction direction : Direction.values()) {
                                if (!device.level.isEmptyBlock(pos.relative(direction))) {
                                    border.add(pos);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            boolean set = false;
            Random rand = device.getRandom();
            while (!border.isEmpty()) {
                int i = rand.nextInt(border.size());
                BlockPos pos = border.get(i);
                Path path = nav.createPath(pos, 1);
                if (path == null) {
                    border.remove(i);
                    air.remove(pos);
                } else {
                    nav.moveTo(path, speed);
                    set = true;
                    break;
                }
            }
            if (!set) {
                while (!air.isEmpty()) {
                    int i = rand.nextInt(air.size());
                    Path path = nav.createPath(air.get(i), 1);
                    if (path == null) {
                        air.remove(i);
                    } else {
                        nav.moveTo(path, speed);
                        set = true;
                        break;
                    }
                }
            }
            if (!set) {
                remove();
            }
        }
        super.tick();
    }

    @Override
    public void stop() {
        getEntity().getNavigation().stop();
    }

    @Override
    public CommandType getType() {
        return CommandType.SCOUT;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putFloat("Speed", speed);
        nbt.putIntArray("Center", new int[]{center.getX(), center.getY(), center.getZ()});
        nbt.putInt("Range", range);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Constants.NBT.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
        if (nbt.contains("Center", Constants.NBT.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("Center");
            center = new BlockPos(arr[0], arr[1], arr[2]);
        }
        if (nbt.contains("Range", Constants.NBT.TAG_INT)) {
            range = nbt.getInt("Range");
        }
    }
}

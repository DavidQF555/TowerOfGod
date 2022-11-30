package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class GroundTechniqueInstance extends ShinsuTechniqueInstance {

    private double startX, startZ, dX, dZ, speed;
    private int period, maxYDif, prevY;

    public GroundTechniqueInstance(Entity user, double dX, double dZ, double speed, int period, int maxYDif) {
        super(user);
        Vec3 start = user == null ? Vec3.ZERO : user.position();
        this.startX = start.x();
        this.startZ = start.z();
        double length = Mth.sqrt((float) (dX * dX + dZ * dZ));
        this.dX = dX / length;
        this.dZ = dZ / length;
        this.speed = speed;
        this.period = period;
        this.maxYDif = maxYDif;
        prevY = (int) start.y();
    }

    public abstract void doEffect(ServerLevel world, Vec3 pos);

    @Override
    public void tick(ServerLevel world) {
        if (getTicks() % period == 0) {
            int count = getTicks() / period;
            double x = startX + dX * speed * (count + 1);
            double z = startZ + dZ * speed * (count + 1);
            BlockPos pos = null;
            for (int dY = -maxYDif; dY < maxYDif; dY++) {
                BlockPos test = new BlockPos(x, prevY + dY, z);
                if (world.getBlockState(test).isCollisionShapeFullBlock(world, test) && world.getBlockState(test.above()).getCollisionShape(world, test.above()).isEmpty() && (pos == null || Math.abs(dY) < Math.abs(prevY - pos.getY()))) {
                    pos = test;
                }
            }
            if (pos == null) {
                remove(world);
            } else {
                doEffect(world, new Vec3(x, pos.getY(), z));
                prevY = pos.getY();
            }
        }
        super.tick(world);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("Speed", speed);
        tag.putDouble("StartX", startX);
        tag.putDouble("StartZ", startZ);
        tag.putDouble("DX", dX);
        tag.putDouble("DZ", dZ);
        tag.putInt("Period", period);
        tag.putInt("MaxYDif", maxYDif);
        tag.putInt("PrevY", prevY);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Tag.TAG_DOUBLE)) {
            speed = nbt.getDouble("Speed");
        }
        if (nbt.contains("StartX", Tag.TAG_DOUBLE)) {
            startX = nbt.getDouble("StartX");
        }
        if (nbt.contains("StartZ", Tag.TAG_DOUBLE)) {
            startZ = nbt.getDouble("StartZ");
        }
        if (nbt.contains("DX", Tag.TAG_DOUBLE)) {
            dX = nbt.getDouble("DX");
        }
        if (nbt.contains("DZ", Tag.TAG_DOUBLE)) {
            dZ = nbt.getDouble("DZ");
        }
        if (nbt.contains("Period", Tag.TAG_INT)) {
            period = nbt.getInt("Period");
        }
        if (nbt.contains("MaxYDif", Tag.TAG_INT)) {
            maxYDif = nbt.getInt("MaxYDif");
        }
        if (nbt.contains("PrevY", Tag.TAG_INT)) {
            prevY = nbt.getInt("PrevY");
        }
    }

}

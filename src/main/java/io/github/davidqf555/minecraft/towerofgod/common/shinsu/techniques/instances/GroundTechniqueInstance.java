package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public abstract class GroundTechniqueInstance extends ShinsuTechniqueInstance {

    private double startX, startZ, dX, dZ, speed;
    private int period, maxYDif, prevY;

    public GroundTechniqueInstance(LivingEntity user, double dX, double dZ, double speed, int period, int maxYDif) {
        super(user);
        Vector3d start = user == null ? Vector3d.ZERO : user.position();
        this.startX = start.x();
        this.startZ = start.z();
        double length = MathHelper.sqrt(dX * dX + dZ * dZ);
        this.dX = dX / length;
        this.dZ = dZ / length;
        this.speed = speed;
        this.period = period;
        this.maxYDif = maxYDif;
        prevY = (int) start.y();
    }

    public abstract void doEffect(ServerWorld world, Vector3d pos);

    @Override
    public void tick(ServerWorld world) {
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
                doEffect(world, new Vector3d(x, pos.getY(), z));
                prevY = pos.getY();
            }
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
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
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Constants.NBT.TAG_DOUBLE)) {
            speed = nbt.getDouble("Speed");
        }
        if (nbt.contains("StartX", Constants.NBT.TAG_DOUBLE)) {
            startX = nbt.getDouble("StartX");
        }
        if (nbt.contains("StartZ", Constants.NBT.TAG_DOUBLE)) {
            startZ = nbt.getDouble("StartZ");
        }
        if (nbt.contains("DX", Constants.NBT.TAG_DOUBLE)) {
            dX = nbt.getDouble("DX");
        }
        if (nbt.contains("DZ", Constants.NBT.TAG_DOUBLE)) {
            dZ = nbt.getDouble("DZ");
        }
        if (nbt.contains("Period", Constants.NBT.TAG_INT)) {
            period = nbt.getInt("Period");
        }
        if (nbt.contains("MaxYDif", Constants.NBT.TAG_INT)) {
            maxYDif = nbt.getInt("MaxYDif");
        }
        if (nbt.contains("PrevY", Constants.NBT.TAG_INT)) {
            prevY = nbt.getInt("PrevY");
        }
    }

}

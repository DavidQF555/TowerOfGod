package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;
import java.util.Random;

public abstract class AreaTechnique extends ShinsuTechniqueInstance {

    private static final int TRIES = 16;
    private double minRadius, radius;
    private int maxY, period;

    public AreaTechnique(LivingEntity user, double minRadius, double radius, int maxY, int period) {
        super(user);
        this.minRadius = minRadius;
        this.radius = radius;
        this.maxY = maxY;
        this.period = period;
    }

    @Override
    public void tick(ServerWorld world) {
        if (world.getGameTime() % period == 0) {
            Entity user = getUser(world);
            Random rand = world.getRandom();
            for (int i = 0; i < TRIES; i++) {
                float degree = rand.nextFloat() * (float) Math.PI * 2;
                double x = user.getX() + MathHelper.cos(degree) * (minRadius + rand.nextDouble() * (radius - minRadius));
                double z = user.getZ() + MathHelper.sin(degree) * (minRadius + rand.nextDouble() * (radius - minRadius));
                Optional<Integer> y = Optional.empty();
                for (int dY = -maxY; dY <= maxY; dY++) {
                    BlockPos test = new BlockPos(x, user.getY() + dY, z);
                    if (world.getBlockState(test).isCollisionShapeFullBlock(world, test) && world.getBlockState(test.above()).getCollisionShape(world, test.above()).isEmpty() && (!y.isPresent() || Math.abs(dY) < Math.abs(user.getY() - y.get()))) {
                        y = Optional.of(test.getY());
                    }
                }
                if (y.isPresent()) {
                    doEffect(world, new Vector3d(x, y.get() + 0.5, z));
                    break;
                }
            }
        }
        super.tick(world);
    }

    protected abstract void doEffect(ServerWorld world, Vector3d pos);

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putDouble("MinRadius", minRadius);
        tag.putDouble("Radius", radius);
        tag.putInt("MaxY", maxY);
        tag.putInt("Period", period);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("MinRadius", Constants.NBT.TAG_DOUBLE)) {
            minRadius = nbt.getDouble("MinRadius");
        }
        if (nbt.contains("Radius", Constants.NBT.TAG_DOUBLE)) {
            radius = nbt.getDouble("Radius");
        }
        if (nbt.contains("MaxY", Constants.NBT.TAG_INT)) {
            maxY = nbt.getInt("MaxY");
        }
        if (nbt.contains("Period", Constants.NBT.TAG_INT)) {
            period = nbt.getInt("Period");
        }
    }

}

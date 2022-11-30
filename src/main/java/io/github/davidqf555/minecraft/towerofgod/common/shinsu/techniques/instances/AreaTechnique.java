package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public abstract class AreaTechnique extends ShinsuTechniqueInstance {

    private static final int TRIES = 16;
    private double minRadius, radius;
    private int maxY, period;

    public AreaTechnique(Entity user, double minRadius, double radius, int maxY, int period) {
        super(user);
        this.minRadius = minRadius;
        this.radius = radius;
        this.maxY = maxY;
        this.period = period;
    }

    @Override
    public void tick(ServerLevel world) {
        if (world.getGameTime() % period == 0) {
            Entity user = getUser(world);
            RandomSource rand = world.getRandom();
            for (int i = 0; i < TRIES; i++) {
                float degree = rand.nextFloat() * (float) Math.PI * 2;
                double x = user.getX() + Mth.cos(degree) * (minRadius + rand.nextDouble() * (radius - minRadius));
                double z = user.getZ() + Mth.sin(degree) * (minRadius + rand.nextDouble() * (radius - minRadius));
                Optional<Integer> y = Optional.empty();
                for (int dY = -maxY; dY <= maxY; dY++) {
                    BlockPos test = new BlockPos(x, user.getY() + dY, z);
                    if (world.getBlockState(test).isCollisionShapeFullBlock(world, test) && world.getBlockState(test.above()).getCollisionShape(world, test.above()).isEmpty() && (y.isEmpty() || Math.abs(dY) < Math.abs(user.getY() - y.get()))) {
                        y = Optional.of(test.getY());
                    }
                }
                if (y.isPresent()) {
                    doEffect(world, new Vec3(x, y.get() + 0.5, z));
                    break;
                }
            }
        }
        super.tick(world);
    }

    protected abstract void doEffect(ServerLevel world, Vec3 pos);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("MinRadius", minRadius);
        tag.putDouble("Radius", radius);
        tag.putInt("MaxY", maxY);
        tag.putInt("Period", period);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("MinRadius", Tag.TAG_DOUBLE)) {
            minRadius = nbt.getDouble("MinRadius");
        }
        if (nbt.contains("Radius", Tag.TAG_DOUBLE)) {
            radius = nbt.getDouble("Radius");
        }
        if (nbt.contains("MaxY", Tag.TAG_INT)) {
            maxY = nbt.getInt("MaxY");
        }
        if (nbt.contains("Period", Tag.TAG_INT)) {
            period = nbt.getInt("Period");
        }
    }

}

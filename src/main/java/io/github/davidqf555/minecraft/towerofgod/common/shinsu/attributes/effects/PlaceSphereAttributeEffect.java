package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class PlaceSphereAttributeEffect<T extends RayTraceResult> implements ShinsuAttributeEffect<T> {

    private final BiFunction<World, BlockPos, BlockState> state;
    private final int radius;

    public PlaceSphereAttributeEffect(int radius, BiFunction<World, BlockPos, BlockState> state) {
        this.radius = radius;
        this.state = state;
    }

    @Override
    public void apply(Entity user, T clip) {
        BlockPos hitPos = new BlockPos(clip.getLocation());
        for (int y = -radius; y <= radius; y++) {
            double xRadius = Math.sqrt(radius * radius - y * y);
            int xRounded = (int) xRadius;
            for (int x = -xRounded; x <= xRounded; x++) {
                int zRounded = (int) Math.sqrt(xRadius * xRadius - x * x);
                for (int z = -zRounded; z <= zRounded; z++) {
                    BlockPos pos = hitPos.offset(x, y, z);
                    BlockState state = this.state.apply(user.level, pos);
                    if (state != null) {
                        user.level.setBlockAndUpdate(pos, state);
                    }
                }
            }
        }
    }

}

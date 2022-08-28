package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.util.function.BiFunction;

public class PlaceSphereAttributeEffect<T extends HitResult> implements ShinsuAttributeEffect<T> {

    private final BiFunction<Level, BlockPos, BlockState> state;
    private final int radius;

    public PlaceSphereAttributeEffect(int radius, BiFunction<Level, BlockPos, BlockState> state) {
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

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

public class DestroyBlockQualityEffect implements ShinsuQualityEffect<BlockRayTraceResult> {

    private final BiPredicate<World, BlockPos> condition;

    public DestroyBlockQualityEffect(BiPredicate<World, BlockPos> condition) {
        this.condition = condition;
    }

    @Override
    public void apply(Entity user, BlockRayTraceResult clip) {
        BlockPos pos = clip.getBlockPos();
        if (condition.test(user.level, pos)) {
            user.level.destroyBlock(pos, true, user);
        }
    }

}

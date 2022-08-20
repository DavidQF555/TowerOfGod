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
        BlockPos pos = clip.getPos();
        if (condition.test(user.world, pos)) {
            user.world.destroyBlock(pos, true, user);
        }
    }

}

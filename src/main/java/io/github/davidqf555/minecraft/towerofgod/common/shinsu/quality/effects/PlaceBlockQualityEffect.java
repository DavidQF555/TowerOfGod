package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class PlaceBlockQualityEffect implements ShinsuQualityEffect<BlockRayTraceResult> {

    private final BiFunction<World, BlockPos, BlockState> state;

    public PlaceBlockQualityEffect(BiFunction<World, BlockPos, BlockState> state) {
        this.state = state;
    }

    @Override
    public void apply(Entity user, BlockRayTraceResult clip) {
        BlockPos pos = clip.getPos().offset(clip.getFace());
        BlockState block = state.apply(user.world, pos);
        user.world.setBlockState(pos, block, 11);
    }

}

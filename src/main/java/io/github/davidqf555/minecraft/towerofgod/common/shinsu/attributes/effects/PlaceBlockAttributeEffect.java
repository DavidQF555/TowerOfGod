package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class PlaceBlockAttributeEffect implements ShinsuAttributeEffect<BlockRayTraceResult> {

    private final BiFunction<World, BlockPos, BlockState> state;

    public PlaceBlockAttributeEffect(BiFunction<World, BlockPos, BlockState> state) {
        this.state = state;
    }

    @Override
    public void apply(Entity user, BlockRayTraceResult clip) {
        BlockPos pos = clip.getBlockPos().relative(clip.getDirection());
        BlockState block = state.apply(user.level, pos);
        user.level.setBlockAndUpdate(pos, block);
    }

}

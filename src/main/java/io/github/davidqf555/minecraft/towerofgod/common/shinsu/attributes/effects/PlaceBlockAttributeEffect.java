package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiFunction;

public class PlaceBlockAttributeEffect implements ShinsuAttributeEffect<BlockHitResult> {

    private final BiFunction<Level, BlockPos, BlockState> state;

    public PlaceBlockAttributeEffect(BiFunction<Level, BlockPos, BlockState> state) {
        this.state = state;
    }

    @Override
    public void apply(Entity user, BlockHitResult clip) {
        BlockPos pos = clip.getBlockPos().relative(clip.getDirection());
        BlockState block = state.apply(user.level, pos);
        user.level.setBlockAndUpdate(pos, block);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TemporaryBlock extends AirBlock {

    public TemporaryBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.destroyBlock(pos, true);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class TemporaryBlock extends Block {

    public TemporaryBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        world.destroyBlock(pos, true);
    }

}

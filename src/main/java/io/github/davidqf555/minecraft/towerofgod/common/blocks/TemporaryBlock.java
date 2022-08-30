package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class TemporaryBlock extends Block {

    public TemporaryBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.destroyBlock(pos, true);
    }

}

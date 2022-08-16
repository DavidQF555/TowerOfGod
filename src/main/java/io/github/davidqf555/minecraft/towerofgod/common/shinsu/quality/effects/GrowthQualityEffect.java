package io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.effects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.server.ServerWorld;

public class GrowthQualityEffect implements ShinsuQualityEffect<BlockRayTraceResult> {

    public static final GrowthQualityEffect INSTANCE = new GrowthQualityEffect();

    protected GrowthQualityEffect() {
    }

    @Override
    public void apply(Entity user, BlockRayTraceResult clip) {
        BlockPos hit = clip.getPos();
        BlockPos pos = hit.offset(clip.getFace());
        BlockState state = user.world.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof IGrowable) {
            if (user.world instanceof ServerWorld && ((IGrowable) b).canGrow(user.world, pos, state, user.world.isRemote())) {
                ((IGrowable) b).grow((ServerWorld) user.world, user.world.rand, pos, state);
            }
        } else {
            state = user.world.getBlockState(hit);
            b = state.getBlock();
            if (b instanceof IGrowable) {
                if (user.world instanceof ServerWorld && ((IGrowable) b).canGrow(user.world, hit, state, user.world.isRemote())) {
                    ((IGrowable) b).grow((ServerWorld) user.world, user.world.rand, hit, state);
                }
            }
        }
    }
}

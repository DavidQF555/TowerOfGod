package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.server.ServerWorld;

public class GrowthAttributeEffect implements ShinsuAttributeEffect<BlockRayTraceResult> {

    public static final GrowthAttributeEffect INSTANCE = new GrowthAttributeEffect();

    protected GrowthAttributeEffect() {
    }

    @Override
    public void apply(Entity user, BlockRayTraceResult clip) {
        BlockPos hit = clip.getBlockPos();
        BlockPos pos = hit.relative(clip.getDirection());
        BlockState state = user.level.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof IGrowable) {
            if (user.level instanceof ServerWorld && ((IGrowable) b).isValidBonemealTarget(user.level, pos, state, user.level.isClientSide())) {
                ((IGrowable) b).performBonemeal((ServerWorld) user.level, user.level.random, pos, state);
            }
        } else {
            state = user.level.getBlockState(hit);
            b = state.getBlock();
            if (b instanceof IGrowable) {
                if (user.level instanceof ServerWorld && ((IGrowable) b).isValidBonemealTarget(user.level, hit, state, user.level.isClientSide())) {
                    ((IGrowable) b).performBonemeal((ServerWorld) user.level, user.level.random, hit, state);
                }
            }
        }
    }
}

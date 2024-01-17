package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GrowthAttributeEffect implements ShinsuAttributeEffect<BlockHitResult> {

    public static final GrowthAttributeEffect INSTANCE = new GrowthAttributeEffect();

    protected GrowthAttributeEffect() {
    }

    @Override
    public void apply(Entity user, BlockHitResult clip) {
        BlockPos hit = clip.getBlockPos();
        BlockPos pos = hit.relative(clip.getDirection());
        BlockState state = user.level().getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof BonemealableBlock) {
            if (user.level() instanceof ServerLevel && ((BonemealableBlock) b).isValidBonemealTarget(user.level(), pos, state)) {
                ((BonemealableBlock) b).performBonemeal((ServerLevel) user.level(), user.level().random, pos, state);
            }
        } else {
            state = user.level().getBlockState(hit);
            b = state.getBlock();
            if (b instanceof BonemealableBlock) {
                if (user.level() instanceof ServerLevel && ((BonemealableBlock) b).isValidBonemealTarget(user.level(), hit, state)) {
                    ((BonemealableBlock) b).performBonemeal((ServerLevel) user.level(), user.level().random, hit, state);
                }
            }
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiPredicate;

public class DestroyBlockAttributeEffect implements ShinsuAttributeEffect<BlockHitResult> {

    private final BiPredicate<Level, BlockPos> condition;

    public DestroyBlockAttributeEffect(BiPredicate<Level, BlockPos> condition) {
        this.condition = condition;
    }

    @Override
    public void apply(Entity user, BlockHitResult clip) {
        BlockPos pos = clip.getBlockPos();
        if (condition.test(user.level, pos)) {
            user.level.destroyBlock(pos, true, user);
        }
    }

}

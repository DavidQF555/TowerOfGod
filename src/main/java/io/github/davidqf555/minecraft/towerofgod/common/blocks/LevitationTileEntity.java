package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import io.github.davidqf555.minecraft.towerofgod.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevitationTileEntity extends BlockEntity {

    private static final int RADIUS = 4;
    private static final int LEVEL = 2;
    private static final int PERIOD = 60;

    public LevitationTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityRegistry.SUSPENDIUM.get(), pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, LevitationTileEntity te) {
        if (world.getGameTime() % PERIOD == 0) {
            AABB bounds = AABB.ofSize(Vec3.atCenterOf(pos), RADIUS * 2, RADIUS * 2, RADIUS * 2);
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, bounds, entity -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= RADIUS * RADIUS)) {
                entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, PERIOD, LEVEL - 1));
            }
        }
    }

}

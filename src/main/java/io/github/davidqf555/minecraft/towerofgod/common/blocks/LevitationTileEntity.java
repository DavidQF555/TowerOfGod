package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import io.github.davidqf555.minecraft.towerofgod.registration.TileEntityRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LevitationTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int RADIUS = 4;
    private static final int LEVEL = 2;
    private static final int PERIOD = 60;

    public LevitationTileEntity() {
        super(TileEntityRegistry.SUSPENDIUM.get());
    }

    @Override
    public void tick() {
        World world = getLevel();
        if (world != null && world.getGameTime() % PERIOD == 0) {
            BlockPos pos = getBlockPos();
            AxisAlignedBB bounds = AxisAlignedBB.ofSize(RADIUS * 2, RADIUS * 2, RADIUS * 2).move(pos);
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, bounds, entity -> EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity) && entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= RADIUS * RADIUS)) {
                entity.addEffect(new EffectInstance(Effects.LEVITATION, PERIOD, LEVEL - 1));
            }
        }
    }
}

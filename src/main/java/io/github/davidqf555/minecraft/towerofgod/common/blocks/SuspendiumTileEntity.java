package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import io.github.davidqf555.minecraft.towerofgod.registration.TileEntityRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SuspendiumTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int RADIUS = 4;
    private static final int LEVEL = 2;
    private static final int PERIOD = 60;

    public SuspendiumTileEntity() {
        super(TileEntityRegistry.SUSPENDIUM.get());
    }

    @Override
    public void tick() {
        World world = getWorld();
        if (world != null && world.getGameTime() % PERIOD == 0) {
            BlockPos pos = getPos();
            AxisAlignedBB bounds = AxisAlignedBB.withSizeAtOrigin(RADIUS * 2, RADIUS * 2, RADIUS * 2).offset(pos);
            for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, bounds, entity -> entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= RADIUS * RADIUS)) {
                entity.addPotionEffect(new EffectInstance(Effects.LEVITATION, PERIOD, LEVEL - 1));
            }
        }
    }
}

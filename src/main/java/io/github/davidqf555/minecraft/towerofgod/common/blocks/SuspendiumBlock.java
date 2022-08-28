package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import io.github.davidqf555.minecraft.towerofgod.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

public class SuspendiumBlock extends BaseEntityBlock {

    public SuspendiumBlock() {
        super(Block.Properties.of(Material.STONE)
                .strength(3f, 3f)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuspendiumTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.SUSPENDIUM.get(), SuspendiumTileEntity::tick);
    }

}

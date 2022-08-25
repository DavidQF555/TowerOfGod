package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class SuspendiumBlock extends Block {

    public SuspendiumBlock() {
        super(Block.Properties.of(Material.STONE)
                .strength(3f, 3f)
                .sound(SoundType.STONE).harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
        );
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SuspendiumTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}

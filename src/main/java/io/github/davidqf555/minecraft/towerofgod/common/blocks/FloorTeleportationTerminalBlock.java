package io.github.davidqf555.minecraft.towerofgod.common.blocks;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenFloorTeleportationTerminalMessage;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FloorTeleportationTerminalBlock extends Block {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
    private static final VoxelShape POST_SHAPE = Block.makeCuboidShape(4, 2, 4, 12, 14, 12);
    private static final VoxelShape COMMON_SHAPE = VoxelShapes.or(BASE_SHAPE, POST_SHAPE);
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.or(COMMON_SHAPE, TOP_PLATE_SHAPE);
    private static final VoxelShape WEST_SHAPE = VoxelShapes.or(Block.makeCuboidShape(1, 10, 0, 5.333333, 14, 16), Block.makeCuboidShape(5.333333, 12, 0, 9.666667, 16, 16), Block.makeCuboidShape(9.666667, 14, 0, 14, 18, 16), COMMON_SHAPE);
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0, 10, 1, 16, 14, 5.333333), Block.makeCuboidShape(0, 12, 5.333333, 16, 16, 9.666667), Block.makeCuboidShape(0, 14, 9.666667, 16, 18, 14), COMMON_SHAPE);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.or(Block.makeCuboidShape(15, 10, 0, 10.666667, 14, 16), Block.makeCuboidShape(10.666667, 12, 0, 6.333333, 16, 16), Block.makeCuboidShape(6.333333, 14, 0, 2, 18, 16), COMMON_SHAPE);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0, 10, 15, 16, 14, 10.666667), Block.makeCuboidShape(0, 12, 10.666667, 16, 16, 6.333333), Block.makeCuboidShape(0, 14, 6.333333, 16, 18, 2), COMMON_SHAPE);
    private static final VoxelShape TOP_PLATE_SHAPE = Block.makeCuboidShape(0, 15, 0, 16, 15, 16);

    public FloorTeleportationTerminalBlock() {
        super(Properties.create(Material.IRON)
                .setRequiresTool()
                .hardnessAndResistance(50, 1200)
        );
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenFloorTeleportationTerminalMessage(ShinsuStats.get(player).getLevel(), pos, state.get(FACING)));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return COMMON_SHAPE;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.world;

import io.github.davidqf555.minecraft.towerofgod.common.blocks.FloorTeleportationTerminalBlock;
import io.github.davidqf555.minecraft.towerofgod.registration.BlockRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.PointOfInterestRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FloorTeleporter extends Teleporter {

    private static final int DISTANCE = 16;
    private final BlockPos teleporter;
    private final Direction direction;

    public FloorTeleporter(ServerWorld worldIn, BlockPos teleporter, Direction direction) {
        super(worldIn);
        this.teleporter = teleporter;
        this.direction = direction;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        DimensionType target = destWorld.getDimensionType();
        double scale = DimensionType.getCoordinateDifference(world.getDimensionType(), target);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos portal = teleporter.offset(direction);
        BlockPos scaled = new BlockPos(portal.getX() * scale, portal.getY(), portal.getZ() * scale);
        BlockPos clamped = new BlockPos(MathHelper.clamp(scaled.getX(), border.minX(), border.maxX()), MathHelper.clamp(scaled.getY(), 1, target.getLogicalHeight()), MathHelper.clamp(scaled.getZ(), border.minZ(), border.maxZ()));
        return getOrCreatePortal(clamped, direction).map(result -> {
            Vector3d vec = new Vector3d(result.startPos.getX() + 0.5, result.startPos.getY(), result.startPos.getZ() + 0.5);
            return new PortalInfo(vec, entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
        }).orElse(null);
    }

    private Optional<TeleportationRepositioner.Result> getOrCreatePortal(BlockPos pos, Direction direction) {
        Optional<TeleportationRepositioner.Result> existing = getExistingPortal(pos, false);
        if (existing.isPresent()) {
            return existing;
        } else {
            return makePortal(pos, direction);
        }
    }

    @Override
    public Optional<TeleportationRepositioner.Result> getExistingPortal(BlockPos pos, boolean isNether) {
        BlockPos teleporter = pos.offset(direction.getOpposite());
        PointOfInterestManager manager = world.getPointOfInterestManager();
        manager.ensureLoadedAndValid(world, teleporter, DISTANCE);
        Optional<PointOfInterest> optional = manager.getInSquare(poiType -> poiType == PointOfInterestRegistry.FLOOR_TELEPORTATION_TERMINAL.get(), teleporter, DISTANCE, PointOfInterestManager.Status.ANY)
                .filter(poi -> world.getBlockState(poi.getPos()).hasProperty(FloorTeleportationTerminalBlock.FACING))
                .filter(poi -> {
                    BlockPos poiPos = poi.getPos();
                    BlockPos middle = poiPos.offset(world.getBlockState(poiPos).get(FloorTeleportationTerminalBlock.FACING));
                    BlockPos up = middle.up();
                    BlockPos down = middle.down();
                    return world.isAirBlock(up) && world.isAirBlock(middle) && world.getBlockState(down).isSolid();
                })
                .min(Comparator.<PointOfInterest>comparingDouble(poi -> poi.getPos().distanceSq(teleporter)).thenComparingInt(poi -> poi.getPos().getY()));
        return optional.map(poi -> {
            BlockPos blockpos = poi.getPos().offset(world.getBlockState(poi.getPos()).get(FloorTeleportationTerminalBlock.FACING));
            return new TeleportationRepositioner.Result(blockpos, 1, 1);
        });
    }

    @Override
    public Optional<TeleportationRepositioner.Result> makePortal(BlockPos pos, Direction.Axis axis) {
        return makePortal(pos, Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE));
    }

    private Optional<TeleportationRepositioner.Result> makePortal(BlockPos pos, Direction direction) {
        for (int dX = -3; dX <= 3; dX++) {
            for (int dY = -1; dY <= 2; dY++) {
                for (int dZ = -3; dZ <= 3; dZ++) {
                    BlockPos check = pos.add(dX, dY, dZ);
                    if (dY == -1) {
                        world.setBlockState(check, Blocks.QUARTZ_BLOCK.getDefaultState());
                    } else {
                        world.setBlockState(check, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        world.setBlockState(pos.offset(direction.getOpposite()), BlockRegistry.FLOOR_TELEPORTATION_TERMINAL.get().getDefaultState().with(FloorTeleportationTerminalBlock.FACING, direction));
        return Optional.of(new TeleportationRepositioner.Result(teleporter.offset(direction), 1, 1));
    }
}

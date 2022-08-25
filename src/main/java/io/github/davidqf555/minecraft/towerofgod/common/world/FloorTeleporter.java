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
        DimensionType target = destWorld.dimensionType();
        double scale = DimensionType.getTeleportationScale(level.dimensionType(), target);
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos portal = teleporter.relative(direction);
        BlockPos scaled = new BlockPos(portal.getX() * scale, portal.getY(), portal.getZ() * scale);
        BlockPos clamped = new BlockPos(MathHelper.clamp(scaled.getX(), border.getMinX(), border.getMaxX()), MathHelper.clamp(scaled.getY(), 1, target.logicalHeight()), MathHelper.clamp(scaled.getZ(), border.getMinZ(), border.getMaxZ()));
        return getOrCreatePortal(clamped, direction).map(result -> {
            Vector3d vec = new Vector3d(result.minCorner.getX() + 0.5, result.minCorner.getY(), result.minCorner.getZ() + 0.5);
            return new PortalInfo(vec, entity.getDeltaMovement(), entity.yRot, entity.xRot);
        }).orElse(null);
    }

    private Optional<TeleportationRepositioner.Result> getOrCreatePortal(BlockPos pos, Direction direction) {
        Optional<TeleportationRepositioner.Result> existing = findPortalAround(pos, false);
        if (existing.isPresent()) {
            return existing;
        } else {
            return makePortal(pos, direction);
        }
    }

    @Override
    public Optional<TeleportationRepositioner.Result> findPortalAround(BlockPos pos, boolean isNether) {
        BlockPos teleporter = pos.relative(direction.getOpposite());
        PointOfInterestManager manager = level.getPoiManager();
        manager.ensureLoadedAndValid(level, teleporter, DISTANCE);
        Optional<PointOfInterest> optional = manager.getInSquare(poiType -> poiType == PointOfInterestRegistry.FLOOR_TELEPORTATION_TERMINAL.get(), teleporter, DISTANCE, PointOfInterestManager.Status.ANY)
                .filter(poi -> level.getBlockState(poi.getPos()).hasProperty(FloorTeleportationTerminalBlock.FACING))
                .filter(poi -> {
                    BlockPos poiPos = poi.getPos();
                    BlockPos middle = poiPos.relative(level.getBlockState(poiPos).getValue(FloorTeleportationTerminalBlock.FACING));
                    BlockPos up = middle.above();
                    BlockPos down = middle.below();
                    return level.isEmptyBlock(up) && level.isEmptyBlock(middle) && level.getBlockState(down).canOcclude();
                })
                .min(Comparator.<PointOfInterest>comparingDouble(poi -> poi.getPos().distSqr(teleporter)).thenComparingInt(poi -> poi.getPos().getY()));
        return optional.map(poi -> {
            BlockPos blockpos = poi.getPos().relative(level.getBlockState(poi.getPos()).getValue(FloorTeleportationTerminalBlock.FACING));
            return new TeleportationRepositioner.Result(blockpos, 1, 1);
        });
    }

    @Override
    public Optional<TeleportationRepositioner.Result> createPortal(BlockPos pos, Direction.Axis axis) {
        return makePortal(pos, Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE));
    }

    private Optional<TeleportationRepositioner.Result> makePortal(BlockPos pos, Direction direction) {
        for (int dX = -3; dX <= 3; dX++) {
            for (int dY = -1; dY <= 2; dY++) {
                for (int dZ = -3; dZ <= 3; dZ++) {
                    BlockPos check = pos.offset(dX, dY, dZ);
                    if (dY == -1) {
                        level.setBlockAndUpdate(check, Blocks.QUARTZ_BLOCK.defaultBlockState());
                    } else {
                        level.setBlockAndUpdate(check, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        level.setBlockAndUpdate(pos.relative(direction.getOpposite()), BlockRegistry.FLOOR_TELEPORTATION_TERMINAL.get().defaultBlockState().setValue(FloorTeleportationTerminalBlock.FACING, direction));
        return Optional.of(new TeleportationRepositioner.Result(teleporter.relative(direction), 1, 1));
    }
}

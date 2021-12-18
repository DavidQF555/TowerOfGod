package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class MoveDevices extends BasicCommandTechnique {

    public MoveDevices(LivingEntity user, int level, Vector3d dir) {
        super(user, level, dir);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.MOVE_DEVICES;
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 2;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 3;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        Entity user = getUser(world);
        Vector3d eye = user.getEyePosition(1);
        int level = getLevel();
        double range = level * 4;
        Vector3d end = eye.add(user.getLookVec().scale(range));
        EntityRayTraceResult trace = ProjectileHelper.rayTraceEntities(world, user, eye, end, AxisAlignedBB.fromVector(eye).grow(range), null);
        Vector3d target = trace == null ? world.rayTraceBlocks(new RayTraceContext(eye, end, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity)).getHitVec() : trace.getHitVec();
        return new MoveCommand(entity, getID(), target, 1 + level / 20f);
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<MoveDevices> {

        @Override
        public Either<MoveDevices, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            MoveDevices technique = new MoveDevices(user, level, dir);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(ErrorMessages.REQUIRES_DEVICE);
        }

        @Override
        public MoveDevices emptyBuild() {
            return new MoveDevices(null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.MOVE_DEVICES;
        }
    }
}

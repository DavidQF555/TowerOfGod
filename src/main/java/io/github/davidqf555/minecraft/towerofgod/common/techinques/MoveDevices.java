package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class MoveDevices extends BasicCommandTechnique {

    public MoveDevices(LivingEntity user, @Nullable String settings, int level, Vector3d dir) {
        super(settings, user, level, dir);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.MOVE_DEVICES;
    }

    @Override
    public boolean isTarget(FlyingDevice device) {
        String settings = getSettings();
        return settings.equals("all") || device.getColor() == DyeColor.valueOf(settings);
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 2;
    }

    @Override
    public boolean isConflicting(ShinsuTechniqueInstance instance) {
        if (instance.getTechnique() == ShinsuTechnique.MOVE_DEVICES) {
            String settings = getSettings();
            return settings.equals("all") || settings.equals(instance.getSettings());
        }
        return false;
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

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<MoveDevices> {

        @Override
        public MoveDevices build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            MoveDevices technique = new MoveDevices(user, settings, level, dir);
            return technique.getDevices().size() > 0 ? technique : null;
        }

        @Nonnull
        @Override
        public MoveDevices emptyBuild() {
            return new MoveDevices(null, null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.MOVE_DEVICES;
        }
    }
}

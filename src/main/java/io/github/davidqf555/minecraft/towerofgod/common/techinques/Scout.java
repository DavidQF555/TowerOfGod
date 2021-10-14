package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ScoutCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Scout extends BasicCommandTechnique {

    public Scout(LivingEntity user, String settings, int level, Vector3d dir) {
        super(settings, user, level, dir);
    }

    @Override
    public int getInitialDuration() {
        return 600;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.SCOUT;
    }

    @Override
    public boolean isTarget(FlyingDevice device) {
        String settings = getSettings();
        return device instanceof ObserverEntity && (settings.equals("all") || device.getColor() == DyeColor.valueOf(settings));
    }

    @Override
    public boolean isConflicting(ShinsuTechniqueInstance instance) {
        if (instance.getTechnique() == ShinsuTechnique.SCOUT) {
            String settings = getSettings();
            return settings.equals("all") || settings.equals(instance.getSettings());
        }
        return false;
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 5;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 3;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        int level = getLevel();
        Entity user = getUser(world);
        Vector3d eye = user.getEyePosition(1);
        double range = 4 + level * 4;
        Vector3d end = eye.add(user.getLookVec().scale(range));
        BlockPos target;
        EntityRayTraceResult trace = ProjectileHelper.rayTraceEntities(world, user, eye, end, AxisAlignedBB.fromVector(eye).grow(range), null);
        if (trace == null) {
            BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eye, end, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity));
            target = result.getPos().offset(result.getFace());
        } else {
            target = trace.getEntity().getPosition();
        }
        return new ScoutCommand(entity, getID(), target, 1 + level / 20f, 8 + 4 * level, getDuration());
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<Scout> {

        @Override
        public Scout build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            Scout technique = new Scout(user, settings, level, dir);
            return technique.getDevices().size() > 0 ? technique : null;
        }

        @Override
        public Scout emptyBuild() {
            return new Scout(null, null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.MOVE_DEVICES;
        }
    }
}

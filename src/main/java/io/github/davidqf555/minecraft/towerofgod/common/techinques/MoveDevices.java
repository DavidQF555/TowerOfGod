package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class MoveDevices extends BasicCommandTechnique {

    public MoveDevices(LivingEntity user, @Nullable String settings, int level, Vector3d dir) {
        super(settings, user, level, dir, 1);
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
        return getDevices().size() * 10;
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
        double range = 32 + getLevel() * 16;
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eye, eye.add(user.getLookVec().scale(range)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity));
        Vector3d vec = result.getHitVec();
        return new MoveCommand(entity, getID(), vec, 1);
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

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseFlowControlCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class LighthouseFlowControl extends BasicCommandTechnique {

    private static final int DURATION = 600;

    public LighthouseFlowControl(LivingEntity user, @Nullable String settings, int level) {
        super(settings, user, level, Vector3d.ZERO, DURATION);
    }

    @Override
    public boolean isTarget(FlyingDevice device) {
        String settings = getSettings();
        return device instanceof LighthouseEntity && (settings.equals("all") || device.getColor() == DyeColor.valueOf(settings));
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.LIGHTHOUSE_FLOW_CONTROL;
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 15;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 2;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        return new LighthouseFlowControlCommand(entity, getID(), 3 + getLevel(), DURATION);
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<LighthouseFlowControl> {

        @Override
        public LighthouseFlowControl build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            LighthouseFlowControl technique = new LighthouseFlowControl(user, settings, level);
            return technique.getDevices().size() > 0 ? technique : null;
        }

        @Nonnull
        @Override
        public LighthouseFlowControl emptyBuild() {
            return new LighthouseFlowControl(null, null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.LIGHTHOUSE_FLOW_CONTROL;
        }
    }
}

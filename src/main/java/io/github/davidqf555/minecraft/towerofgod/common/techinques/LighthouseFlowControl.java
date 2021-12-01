package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseFlowControlCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class LighthouseFlowControl extends BasicCommandTechnique {

    public LighthouseFlowControl(LivingEntity user, int level) {
        super(user, level, Vector3d.ZERO);
    }

    @Override
    public int getInitialDuration() {
        return 40 + getLevel() * 20;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.LIGHTHOUSE_FLOW_CONTROL;
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 10;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 2;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        return new LighthouseFlowControlCommand(entity, getID(), 3 + getLevel(), getDuration());
    }

    @Override
    public int getCooldown() {
        return getInitialDuration() + 200;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<LighthouseFlowControl> {

        @Override
        public Either<LighthouseFlowControl, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            LighthouseFlowControl technique = new LighthouseFlowControl(user, level);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(ErrorMessages.REQUIRES_DEVICE);
        }

        @Override
        public LighthouseFlowControl emptyBuild() {
            return new LighthouseFlowControl(null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.LIGHTHOUSE_FLOW_CONTROL;
        }
    }
}

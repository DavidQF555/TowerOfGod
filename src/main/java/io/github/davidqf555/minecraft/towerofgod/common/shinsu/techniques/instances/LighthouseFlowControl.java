package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseFlowControlCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class LighthouseFlowControl extends BasicCommandTechnique {

    private int duration;
    private double range;

    public LighthouseFlowControl(Entity user, int duration, double range) {
        super(user);
        this.duration = duration;
        this.range = range;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.LIGHTHOUSE_FLOW_CONTROL.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 25;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        return new LighthouseFlowControlCommand(entity, getID(), range, getDuration());
    }

    @Override
    public int getCooldown() {
        return 600;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Range", Constants.NBT.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("Range", range);
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<LighthouseFlowControl> {

        @Override
        public Either<LighthouseFlowControl, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            LighthouseFlowControl technique = new LighthouseFlowControl(user, 60, 3);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public LighthouseFlowControl blankCreate() {
            return new LighthouseFlowControl(null, 0, 0);
        }

    }
}

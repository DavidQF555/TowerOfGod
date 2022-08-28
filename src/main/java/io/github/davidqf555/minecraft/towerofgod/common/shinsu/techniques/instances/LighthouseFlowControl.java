package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseFlowControlCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class LighthouseFlowControl extends BasicCommandTechnique {

    private int duration;
    private double range;

    public LighthouseFlowControl(LivingEntity user, int duration, double range) {
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
        return getDevices().size() * 10;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 2;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerLevel world) {
        return new LighthouseFlowControlCommand(entity, getID(), range, getDuration());
    }

    @Override
    public int getCooldown() {
        return getDuration() + 200;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Range", Tag.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("Range", range);
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<LighthouseFlowControl> {

        @Override
        public Either<LighthouseFlowControl, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DEVICE_CONTROL).getLevel();
            LighthouseFlowControl technique = new LighthouseFlowControl(user, 40 + level * 20, 3 + level);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public LighthouseFlowControl blankCreate() {
            return new LighthouseFlowControl(null, 0, 0);
        }

    }
}

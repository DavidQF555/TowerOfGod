package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FollowOwnerCommand;
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

public class FollowOwner extends BasicCommandTechnique {

    private float speed;

    public FollowOwner(LivingEntity user, float speed) {
        super(user);
        this.speed = speed;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FOLLOW_OWNER.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 3;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 3;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerLevel world) {
        return new FollowOwnerCommand(entity, getID(), speed);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Speed", Tag.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putFloat("Speed", speed);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FollowOwner> {

        @Override
        public Either<FollowOwner, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DEVICE_CONTROL).getLevel();
            FollowOwner technique = new FollowOwner(user, 1 + level / 20f);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public FollowOwner blankCreate() {
            return new FollowOwner(null, 1);
        }

    }
}

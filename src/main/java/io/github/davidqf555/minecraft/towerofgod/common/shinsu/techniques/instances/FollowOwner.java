package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FollowOwnerCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class FollowOwner extends BasicCommandTechnique {

    public FollowOwner(Entity user) {
        super(user);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FOLLOW_OWNER.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 10;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerLevel world) {
        return new FollowOwnerCommand(entity, getID(), 1);
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FollowOwner> {

        @Override
        public Either<FollowOwner, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            FollowOwner technique = new FollowOwner(user);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public FollowOwner blankCreate() {
            return new FollowOwner(null);
        }

    }
}

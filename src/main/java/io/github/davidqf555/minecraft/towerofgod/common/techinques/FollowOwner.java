package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FollowOwnerCommand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class FollowOwner extends BasicCommandTechnique {

    public FollowOwner(LivingEntity user, int level) {
        super(user, level, Vector3d.ZERO);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.FOLLOW_OWNER;
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
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        return new FollowOwnerCommand(entity, getID(), 1 + getLevel() / 20f);
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FollowOwner> {

        @Override
        public Either<FollowOwner, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            FollowOwner technique = new FollowOwner(user, level);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(ErrorMessages.REQUIRES_DEVICE);
        }

        @Override
        public FollowOwner emptyBuild() {
            return new FollowOwner(null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.FOLLOW_OWNER;
        }
    }
}

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FollowOwnerCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class FollowOwner extends BasicCommandTechnique {

    public FollowOwner(LivingEntity user, @Nullable String settings, int level) {
        super(settings, user, level, Vector3d.ZERO, 1);
    }

    @Override
    public boolean isTarget(FlyingDevice device) {
        String settings = getSettings();
        return settings.equals("all") || device.getColor() == DyeColor.valueOf(settings);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.FOLLOW_OWNER;
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
        return new FollowOwnerCommand(entity, getID());
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<FollowOwner> {

        @Override
        public FollowOwner build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            FollowOwner technique = new FollowOwner(user, settings, level);
            return technique.getDevices().size() > 0 ? technique : null;
        }

        @Nonnull
        @Override
        public FollowOwner emptyBuild() {
            return new FollowOwner(null, null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.FOLLOW_OWNER;
        }
    }
}

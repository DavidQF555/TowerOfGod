package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class MoveDevices extends BasicCommandTechnique {

    private Vec3 direction;
    private double distance;

    public MoveDevices(Entity user, Vec3 direction, double distance) {
        super(user);
        this.direction = direction;
        this.distance = distance;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.MOVE_DEVICES.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 10;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerLevel world) {
        Entity user = getUser(world);
        Vec3 eye = user.getEyePosition(1);
        Vec3 end = eye.add(direction.scale(distance));
        EntityHitResult trace = ProjectileUtil.getEntityHitResult(world, user, eye, end, AABB.ofSize(eye, distance * 2, distance * 2, distance * 2), null);
        Vec3 target = trace == null ? world.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity)).getLocation() : trace.getLocation();
        return new MoveCommand(entity, getID(), target, 1);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
        if (nbt.contains("Distance", Tag.TAG_DOUBLE)) {
            distance = nbt.getDouble("Distance");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        nbt.putDouble("Distance", distance);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<MoveDevices> {

        @Override
        public Either<MoveDevices, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            MoveDevices technique = new MoveDevices(user, dir, 64);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public MoveDevices blankCreate() {
            return new MoveDevices(null, Vec3.ZERO, 0);
        }

    }
}

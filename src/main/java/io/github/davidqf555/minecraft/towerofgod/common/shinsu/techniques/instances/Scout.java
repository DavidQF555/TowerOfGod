package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ScoutCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Scout extends BasicCommandTechnique {

    private Vec3 direction;
    private double range;
    private int radius;
    private float speed;

    public Scout(Entity user, Vec3 direction, double range, int radius, float speed) {
        super(user);
        this.direction = direction;
        this.range = range;
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    public int getDuration() {
        return 600;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.SCOUT.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 10;
    }

    @Override
    public int getCooldown() {
        return 300;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerLevel world) {
        Entity user = getUser(world);
        Vec3 eye = user.getEyePosition(1);
        Vec3 end = eye.add(direction.scale(range));
        BlockPos target;
        EntityHitResult trace = ProjectileUtil.getEntityHitResult(world, user, eye, end, AABB.ofSize(eye, range * 2, range * 2, range * 2), e -> true);
        if (trace == null) {
            BlockHitResult result = world.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            target = result.getBlockPos().relative(result.getDirection());
        } else {
            target = trace.getEntity().blockPosition();
        }
        return new ScoutCommand(entity, getID(), target, speed, radius, getDuration());
    }


    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
        if (nbt.contains("Speed", Tag.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
        if (nbt.contains("Range", Tag.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
        if (nbt.contains("Radius", Tag.TAG_INT)) {
            radius = nbt.getInt("Radius");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        nbt.putFloat("Speed", speed);
        nbt.putDouble("Range", range);
        nbt.putInt("Radius", radius);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<Scout> {

        @Override
        public Either<Scout, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            Scout technique = new Scout(user, dir, 8, 12, 1);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public Scout blankCreate() {
            return new Scout(null, Vec3.ZERO, 0, 0, 1);
        }

    }
}

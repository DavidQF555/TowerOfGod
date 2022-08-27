package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class MoveDevices extends BasicCommandTechnique {

    private Vector3d direction;
    private double distance;
    private float speed;

    public MoveDevices(LivingEntity user, Vector3d direction, double distance, float speed) {
        super(user);
        this.direction = direction;
        this.distance = distance;
        this.speed = speed;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.MOVE_DEVICES.get();
    }

    @Override
    public int getShinsuUse() {
        return getDevices().size() * 2;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 3;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        Entity user = getUser(world);
        Vector3d eye = user.getEyePosition(1);
        Vector3d end = eye.add(direction.scale(distance));
        EntityRayTraceResult trace = ProjectileHelper.getEntityHitResult(world, user, eye, end, AxisAlignedBB.ofSize(distance * 2, distance * 2, distance * 2), null);
        Vector3d target = trace == null ? world.clip(new RayTraceContext(eye, end, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity)).getLocation() : trace.getLocation();
        return new MoveCommand(entity, getID(), target, speed);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
        if (nbt.contains("Speed", Constants.NBT.TAG_FLOAT)) {
            speed = nbt.getFloat("Speed");
        }
        if (nbt.contains("Distance", Constants.NBT.TAG_DOUBLE)) {
            distance = nbt.getDouble("Distance");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        nbt.putFloat("Speed", speed);
        nbt.putDouble("Distance", distance);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<MoveDevices> {

        @Override
        public Either<MoveDevices, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DEVICE_CONTROL).getLevel();
            MoveDevices technique = new MoveDevices(user, dir, level * 4, 1 + level / 20f);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public MoveDevices blankCreate() {
            return new MoveDevices(null, Vector3d.ZERO, 0, 1);
        }

    }
}

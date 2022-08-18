package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ScoutCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.TypeLevelRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Scout extends BasicCommandTechnique {

    private Vector3d direction;
    private double range;
    private int radius;
    private float speed;

    public Scout(LivingEntity user, Vector3d direction, double range, int radius, float speed) {
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
        return getDevices().size() * 5;
    }

    @Override
    public int getBaangsUse() {
        return 1 + getDevices().size() / 3;
    }

    @Override
    protected DeviceCommand createCommand(FlyingDevice entity, ServerWorld world) {
        Entity user = getUser(world);
        Vector3d eye = user.getEyePosition(1);
        Vector3d end = eye.add(direction.scale(range));
        BlockPos target;
        EntityRayTraceResult trace = ProjectileHelper.rayTraceEntities(world, user, eye, end, AxisAlignedBB.fromVector(eye).grow(range), null);
        if (trace == null) {
            BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eye, end, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity));
            target = result.getPos().offset(result.getFace());
        } else {
            target = trace.getEntity().getPosition();
        }
        return new ScoutCommand(entity, getID(), target, speed, radius, getDuration());
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
        if (nbt.contains("Range", Constants.NBT.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
        if (nbt.contains("Radius", Constants.NBT.TAG_INT)) {
            radius = nbt.getInt("Radius");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("X", direction.getX());
        nbt.putDouble("Y", direction.getY());
        nbt.putDouble("Z", direction.getZ());
        nbt.putFloat("Speed", speed);
        nbt.putDouble("Range", range);
        nbt.putInt("Radius", radius);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<Scout> {

        @Override
        public Either<Scout, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DEVICE_CONTROL).getLevel();
            Scout technique = new Scout(user, dir, 4 + level * 4, 8 + level * 4, 1 + level / 20f);
            return technique.getDevices().size() > 0 ? Either.left(technique) : Either.right(Messages.REQUIRES_DEVICE);
        }

        @Override
        public Scout blankCreate() {
            return new Scout(null, Vector3d.ZERO, 0, 0, 1);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechniqueRegistry.MOVE_DEVICES.get();
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 7)};
        }
    }
}

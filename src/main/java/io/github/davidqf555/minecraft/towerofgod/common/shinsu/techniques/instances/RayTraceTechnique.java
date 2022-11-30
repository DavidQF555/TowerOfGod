package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public abstract class RayTraceTechnique extends ShinsuTechniqueInstance {

    private boolean entityCollision;
    private Vector3d direction;
    private double range;

    public RayTraceTechnique(Entity user, Vector3d direction, double range, boolean entityCollision) {
        super(user);
        this.direction = direction;
        this.range = range;
        this.entityCollision = entityCollision;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        Vector3d start = new Vector3d(user.getX(), user.getEyeY(), user.getZ());
        Vector3d end = start.add(direction.scale(range));
        if (entityCollision) {
            EntityRayTraceResult entity = ProjectileHelper.getEntityHitResult(world, null, start, end, AxisAlignedBB.ofSize(range * 2, range * 2, range * 2).move(start), null);
            if (entity != null) {
                doEffect(world, entity);
                doEntityEffect(world, entity);
                super.onUse(world);
                return;
            }
        }
        doEffect(world, world.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null)));
        super.onUse(world);
    }

    public abstract void doEffect(ServerWorld world, RayTraceResult result);

    public void doEntityEffect(ServerWorld world, EntityRayTraceResult result) {
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
        if (nbt.contains("EntityCollision", Constants.NBT.TAG_BYTE)) {
            entityCollision = nbt.getBoolean("EntityCollision");
        }
        if (nbt.contains("Range", Constants.NBT.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        nbt.putBoolean("EntityCollision", entityCollision);
        nbt.putDouble("Range", range);
        return nbt;
    }

}

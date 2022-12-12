package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class RayTraceTechnique extends ShinsuTechniqueInstance {

    private boolean entityCollision;
    private Vec3 direction;
    private double range;

    public RayTraceTechnique(Entity user, Vec3 direction, double range, boolean entityCollision) {
        super(user);
        this.direction = direction;
        this.range = range;
        this.entityCollision = entityCollision;
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        Vec3 start = new Vec3(user.getX(), user.getEyeY(), user.getZ());
        Vec3 end = start.add(direction.scale(range));
        if (entityCollision) {
            EntityHitResult entity = ProjectileUtil.getEntityHitResult(world, null, start, end, AABB.ofSize(start, range * 2, range * 2, range * 2), e -> true);
            if (entity != null) {
                doEffect(world, entity);
                doEntityEffect(world, entity);
                super.onUse(world);
                return;
            }
        }
        doEffect(world, world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)));
        super.onUse(world);
    }

    public abstract void doEffect(ServerLevel world, HitResult result);

    public void doEntityEffect(ServerLevel world, EntityHitResult result) {
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
        if (nbt.contains("EntityCollision", Tag.TAG_BYTE)) {
            entityCollision = nbt.getBoolean("EntityCollision");
        }
        if (nbt.contains("Range", Tag.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        nbt.putBoolean("EntityCollision", entityCollision);
        nbt.putDouble("Range", range);
        return nbt;
    }

}

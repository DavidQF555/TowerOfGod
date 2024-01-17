package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;

public class Flash extends ShinsuTechniqueInstance {

    private static final double RANGE = 64;
    private Vec3 direction;

    public Flash(Entity user, Vec3 direction) {
        super(user);
        this.direction = direction;
    }

    @Override
    public int getCooldown() {
        return 900;
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(world);
        if (lightning != null) {
            Vec3 start = new Vec3(user.getX(), user.getEyeY(), user.getZ());
            Vec3 end = start.add(direction.multiply(RANGE, RANGE, RANGE));
            EntityHitResult entity = ProjectileUtil.getEntityHitResult(world, lightning, start, end, AABB.ofSize(start, RANGE * 2, RANGE * 2, RANGE * 2), e -> true);
            Vec3 pos = Objects.requireNonNullElseGet(entity, () -> world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, lightning))).getLocation();
            if (user instanceof ServerPlayer) {
                lightning.setCause((ServerPlayer) user);
            }
            lightning.setVisualOnly(true);
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f((float) start.x(), (float) start.y(), (float) start.z()));
            world.addFreshEntity(lightning);
            user.teleportTo(pos.x(), pos.y(), pos.z());
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FLASH.get();
    }

    @Override
    public int getShinsuUse() {
        return 20;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Flash> {

        @Override
        public Either<Flash, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new Flash(user, dir));
        }

        @Override
        public Flash blankCreate() {
            return new Flash(null, Vec3.ZERO);
        }

    }
}

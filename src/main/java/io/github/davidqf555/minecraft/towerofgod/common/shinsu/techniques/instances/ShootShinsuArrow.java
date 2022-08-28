package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class ShootShinsuArrow extends ShinsuTechniqueInstance {

    private Vec3 direction;
    private float velocity;
    private UUID arrow;

    public ShootShinsuArrow(LivingEntity user, Vec3 direction) {
        super(user);
        this.direction = direction;
        arrow = null;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public int getDuration() {
        return 200;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get();
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(world);
            if (arrow != null) {
                ShinsuAttribute attribute = ShinsuStats.get(user).getAttribute();
                arrow.setAttribute(attribute);
                arrow.setTechnique(getID());
                float speed = velocity * 3;
                if (attribute != null) {
                    speed *= attribute.getSpeed();
                }
                arrow.shoot(direction.x(), direction.y(), direction.z(), speed, 1);
                arrow.setOwner(user);
                arrow.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
                this.arrow = arrow.getUUID();
                world.addFreshEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void tick(ServerLevel world) {
        if (arrow == null || world.getEntity(arrow) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (arrow != null) {
            nbt.putUUID("Arrow", arrow);
        }
        nbt.putFloat("Velocity", velocity);
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Arrow", Tag.TAG_INT_ARRAY)) {
            arrow = nbt.getUUID("Arrow");
        }
        if (nbt.contains("Velocity", Tag.TAG_FLOAT)) {
            setVelocity(nbt.getFloat("Velocity"));
        }
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ShootShinsuArrow> {

        @Override
        public Either<ShootShinsuArrow, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new ShootShinsuArrow(user, dir));
        }

        @Override
        public ShootShinsuArrow blankCreate() {
            return new ShootShinsuArrow(null, Vec3.ZERO);
        }

    }
}

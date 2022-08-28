package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
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

public class ShinsuBlast extends ShinsuTechniqueInstance {

    private static final float BASE_SPEED = 0.5f;
    private UUID blast;
    private Vec3 direction;

    public ShinsuBlast(LivingEntity user, Vec3 direction) {
        super(user);
        this.direction = direction;
        blast = null;
    }

    @Override
    public int getDuration() {
        return 400;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.SHINSU_BLAST.get();
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity u = getUser(world);
        if (u instanceof LivingEntity) {
            ShinsuEntity shinsu = EntityRegistry.SHINSU.get().create(world);
            if (shinsu != null) {
                LivingEntity user = (LivingEntity) u;
                shinsu.setOwner(user);
                shinsu.setTechnique(this);
                shinsu.setPos(user.getX(), user.getEyeY() - shinsu.getBoundingBox().getYsize() / 2, user.getZ());
                shinsu.shoot(direction.x(), direction.y(), direction.z(), BASE_SPEED, 0);
                blast = shinsu.getUUID();
                world.addFreshEntity(shinsu);
            }
        }
        super.onUse(world);
    }

    @Override
    public void tick(ServerLevel world) {
        if (blast == null || world.getEntity(blast) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (blast != null) {
            nbt.putUUID("Blast", blast);
        }
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Blast", Tag.TAG_INT_ARRAY)) {
            blast = nbt.getUUID("Blast");
        }
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ShinsuBlast> {

        @Override
        public Either<ShinsuBlast, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new ShinsuBlast(user, dir));
        }

        @Override
        public ShinsuBlast blankCreate() {
            return new ShinsuBlast(null, Vec3.ZERO);
        }

    }
}

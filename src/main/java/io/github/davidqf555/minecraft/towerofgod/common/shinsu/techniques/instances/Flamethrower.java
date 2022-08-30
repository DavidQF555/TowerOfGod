package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Flamethrower extends ShinsuTechniqueInstance {

    private int duration, count;
    private float spread;
    private double magnitude;

    public Flamethrower(LivingEntity user, int duration, float spread, int count, double magnitude) {
        super(user);
        this.duration = duration;
        this.spread = spread;
        this.count = count;
        this.magnitude = magnitude;
    }

    @Override
    public void tick(ServerLevel world) {
        Entity user = getUser(world);
        RandomSource rand = world.getRandom();
        Vec3 center = user.getLookAngle();
        for (int i = 0; i < count; i++) {
            SmallFireball fire = EntityType.SMALL_FIREBALL.create(world);
            if (fire != null) {
                float angle = spread * i / count - spread / 2;
                Vec3 dir = center.yRot(angle * (float) Math.PI / 180).add(rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1);
                fire.setPos(user.getX(), user.getEyeY(), user.getZ());
                fire.setDeltaMovement(dir.scale(magnitude));
                fire.yPower = -0.05;
                fire.setOwner(user);
                world.addFreshEntity(fire);
            }
        }
        super.tick(world);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FLAMETHROWER.get();
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt("Duration", duration);
        tag.putInt("Count", count);
        tag.putFloat("Spread", spread);
        tag.putDouble("Magnitude", magnitude);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Count", Tag.TAG_INT)) {
            count = nbt.getInt("Count");
        }
        if (nbt.contains("Spread", Tag.TAG_FLOAT)) {
            spread = nbt.getFloat("Spread");
        }
        if (nbt.contains("Magnitude", Tag.TAG_DOUBLE)) {
            magnitude = nbt.getDouble("Magnitude");
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Flamethrower> {

        @Override
        public Either<Flamethrower, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            int control = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            int manifest = ShinsuStats.get(user).getData(ShinsuTechniqueType.MANIFEST).getLevel();
            return Either.left(new Flamethrower(user, 60, control * 2 + 30, Math.min(manifest / 5 + 1, 10), Math.min(0.25 + control / 10.0, 3)));
        }

        @Override
        public Flamethrower blankCreate() {
            return new Flamethrower(null, 0, 0, 0, 0);
        }
    }
}

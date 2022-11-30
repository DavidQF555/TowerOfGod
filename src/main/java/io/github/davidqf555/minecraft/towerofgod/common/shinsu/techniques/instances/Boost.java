package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Boost extends ShinsuTechniqueInstance {

    private static final int PARTICLES = 25;
    private Vec3 dir;

    public Boost(Entity user, Vec3 dir) {
        super(user);
        this.dir = dir;
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        float width = user.getBbWidth() / 2;
        world.sendParticles(ParticleTypes.FLAME, user.getX(), user.getY(), user.getZ(), PARTICLES, width, user.getBbHeight() * 0.25, width, 0);
        Vec3 dir = this.dir.scale(ShinsuStats.get(user).getTension() * 2);
        user.push(dir.x(), dir.y(), dir.z());
        user.hurtMarked = true;
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.BOOST.get();
    }

    @Override
    public int getShinsuUse() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 600;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("X", dir.x());
        tag.putDouble("Y", dir.y());
        tag.putDouble("Z", dir.z());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            dir = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Boost> {

        @Override
        public Either<Boost, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new Boost(user, dir));
        }

        @Override
        public Boost blankCreate() {
            return new Boost(null, Vec3.ZERO);
        }
    }

}

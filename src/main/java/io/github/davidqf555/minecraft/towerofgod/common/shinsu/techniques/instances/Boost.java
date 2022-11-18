package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class Boost extends ShinsuTechniqueInstance {

    private static final int PARTICLES = 25;
    private Vector3d dir;

    public Boost(Entity user, Vector3d dir) {
        super(user);
        this.dir = dir;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        float width = user.getBbWidth() / 2;
        world.sendParticles(ParticleTypes.FLAME, user.getX(), user.getY(), user.getZ(), PARTICLES, width, user.getBbHeight() * 0.25, width, 0);
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
        return 15;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putDouble("X", dir.x());
        tag.putDouble("Y", dir.y());
        tag.putDouble("Z", dir.z());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            dir = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Boost> {

        @Override
        public Either<Boost, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new Boost(user, dir.scale(Math.min(2, 5))));
        }

        @Override
        public Boost blankCreate() {
            return new Boost(null, Vector3d.ZERO);
        }
    }

}

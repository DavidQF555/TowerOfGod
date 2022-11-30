package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class FireExplosion extends GroundTechniqueInstance {

    public FireExplosion(Entity user, double dX, double dZ) {
        super(user, dX, dZ, 3, 1, 8);
    }

    @Override
    public int getDuration() {
        return 15;
    }

    @Override
    public void doEffect(ServerWorld world, Vector3d pos) {
        world.explode(getUser(world), pos.x(), pos.y(), pos.z(), 2, true, Explosion.Mode.NONE);
        world.sendParticles(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), 100, 2, 2, 2, 0.2);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.ERUPTION.get();
    }

    @Override
    public int getShinsuUse() {
        return 50;
    }

    public static class Factory implements ShinsuTechnique.IFactory<FireExplosion> {

        @Override
        public Either<FireExplosion, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new FireExplosion(user, dir.x(), dir.z()));
        }

        @Override
        public FireExplosion blankCreate() {
            return new FireExplosion(null, 1, 0);
        }

    }
}

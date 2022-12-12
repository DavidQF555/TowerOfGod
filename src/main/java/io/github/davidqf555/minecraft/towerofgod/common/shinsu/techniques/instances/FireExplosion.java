package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public void doEffect(ServerLevel world, Vec3 pos) {
        world.explode(getUser(world), pos.x(), pos.y(), pos.z(), 2, true, Level.ExplosionInteraction.MOB);
        world.sendParticles(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), 100, 2, 2, 2, 0.2);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.ERUPTION.get();
    }

    @Override
    public int getShinsuUse() {
        return 70;
    }

    @Override
    public int getCooldown() {
        return 2400;
    }

    public static class Factory implements ShinsuTechnique.IFactory<FireExplosion> {

        @Override
        public Either<FireExplosion, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new FireExplosion(user, dir.x(), dir.z()));
        }

        @Override
        public FireExplosion blankCreate() {
            return new FireExplosion(null, 1, 0);
        }

    }
}

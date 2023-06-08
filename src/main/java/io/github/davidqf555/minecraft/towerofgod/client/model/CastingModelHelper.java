package io.github.davidqf555.minecraft.towerofgod.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class CastingModelHelper {

    private CastingModelHelper() {
    }

    public static void transformRightArm(ModelPart rightArm) {
        rightArm.yRot = 0;
        rightArm.xRot = -60 * (float) Math.PI / 180;
        rightArm.zRot = 0;
    }

    public static void transformLeftArm(ModelPart leftArm) {
        leftArm.yRot = 0;
        leftArm.xRot = -60 * (float) Math.PI / 180;
        leftArm.zRot = 0;
    }

    public static Vec3 getParticleSpawnOffset(LivingEntity entity) {
        Vec3 base = new Vec3(0, 1, 0.5);
        return base.yRot(-entity.yBodyRot * (float) Math.PI / 180);
    }

    public static double getParticleRadius(LivingEntity entity) {
        return (entity.getBbWidth() + entity.getBbHeight()) * 0.08 / 2;
    }

    public static int getParticleCount(LivingEntity entity) {
        return (int) Math.ceil(1000 * Math.pow(getParticleRadius(entity), 3));
    }

    public static void spawnParticles(LivingEntity entity, ParticleOptions particle) {
        double spread = getParticleRadius(entity);
        RandomSource random = entity.getRandom();
        Vec3 start = entity.position().add(getParticleSpawnOffset(entity));
        int count = getParticleCount(entity);
        for (int i = 0; i < count; i++) {
            Vec3 spawn = start.add(random.nextGaussian() * spread, random.nextGaussian() * spread, random.nextGaussian() * spread);
            entity.level().addParticle(particle, spawn.x(), spawn.y(), spawn.z(), 0, 0, 0);
        }
    }

}

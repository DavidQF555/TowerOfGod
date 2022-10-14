package io.github.davidqf555.minecraft.towerofgod.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public final class CastingModelHelper {

    private CastingModelHelper() {
    }

    public static void transformRightArm(ModelRenderer rightArm) {
        rightArm.yRot = 0;
        rightArm.xRot = -60 * (float) Math.PI / 180;
        rightArm.zRot = 0;
    }

    public static void transformLeftArm(ModelRenderer leftArm) {
        leftArm.yRot = 0;
        leftArm.xRot = -60 * (float) Math.PI / 180;
        leftArm.zRot = 0;
    }

    public static Vector3d getParticleSpawnOffset(LivingEntity entity) {
        Vector3d base = new Vector3d(0, 1, 0.5);
        return base.yRot(-entity.yBodyRot * (float) Math.PI / 180);
    }

    public static double getParticleRadius(LivingEntity entity) {
        return (entity.getBbWidth() + entity.getBbHeight()) * 0.08 / 2;
    }

    public static int getParticleCount(LivingEntity entity) {
        return (int) Math.ceil(1000 * Math.pow(getParticleRadius(entity), 3));
    }

    public static void spawnParticles(LivingEntity entity, IParticleData particle) {
        double spread = getParticleRadius(entity);
        Random random = entity.getRandom();
        Vector3d start = entity.position().add(getParticleSpawnOffset(entity));
        int count = getParticleCount(entity);
        for (int i = 0; i < count; i++) {
            Vector3d spawn = start.add(random.nextGaussian() * spread, random.nextGaussian() * spread, random.nextGaussian() * spread);
            entity.level.addParticle(particle, spawn.x(), spawn.y(), spawn.z(), 0, 0, 0);
        }
    }

}

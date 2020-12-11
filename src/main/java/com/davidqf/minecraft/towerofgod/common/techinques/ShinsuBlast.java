package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.common.entities.ShinsuEntity;
import com.davidqf.minecraft.towerofgod.common.entities.ShinsuUserEntity;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShinsuBlast extends ShinsuTechnique.Direction {

    private static final double BASE_SPEED = 0.5;

    public ShinsuBlast(LivingEntity user, int level, @Nonnull Vector3d dir) {
        super(ShinsuTechniques.SHINSU_BLAST, user, level, dir.normalize(), 200);
    }

    @Override
    public void onUse(World world) {
        Entity u = getUser(world);
            if (u instanceof LivingEntity) {
                LivingEntity user = (LivingEntity) u;
                IShinsuStats stats = IShinsuStats.get(u);
                ShinsuQuality quality = stats.getQuality();
                double speed = quality.getSpeed();
                speed *= BASE_SPEED * getLevel() / 2.0;
                Vector3d dir = getDirection().mul(speed, speed, speed);
                ShinsuEntity shinsuEntity = new ShinsuEntity(world, user, quality, getLevel(), user.getPosX(), user.getPosYEye(), user.getPosZ(), dir.x, dir.y, dir.z);
                user.getEntityWorld().addEntity(shinsuEntity);
        }
        }

    public static class Builder implements ShinsuTechniques.Builder<ShinsuBlast> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public ShinsuBlast build(@Nonnull LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
                return dir != null ? new ShinsuBlast(user, level, dir) : null;
        }

        @Override
        public boolean canCast(@Nonnull ShinsuTechniques technique, @Nonnull LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return ShinsuTechniques.Builder.super.canCast(technique, user, level, target, dir) && (!(user instanceof MobEntity) || ((MobEntity) user).getAttackTarget() != null);
        }

        @Nonnull
        @Override
        public ShinsuBlast emptyBuild() {
            return new ShinsuBlast(null, 0, Vector3d.ZERO);
        }

        @Override
        public int getShinsuUse() {
            return shinsu;
        }

        @Override
        public int getBaangUse() {
            return baangs;
        }
    }
}

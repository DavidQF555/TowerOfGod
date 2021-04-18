package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ShinsuBlast extends ShinsuTechniqueInstance.Direction {

    private static final double BASE_SPEED = 0.5;
    private static final int DURATION = 400;
    private static final int COOLDOWN = 40;

    public ShinsuBlast(LivingEntity user, int level, Vector3d dir) {
        super(ShinsuTechnique.SHINSU_BLAST, user, level, dir.normalize(), DURATION);
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity u = getUser(world);
        if (u instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) u;
            IShinsuStats stats = IShinsuStats.get(u);
            ShinsuQuality quality = stats.getQuality();
            double speed = quality.getSpeed();
            speed *= BASE_SPEED * getLevel() / 2.0;
            Vector3d dir = getDirection().mul(speed, speed, speed);
            ShinsuEntity shinsuEntity = new ShinsuEntity(world, user, quality, this, getLevel(), user.getPosX(), user.getPosYEye(), user.getPosZ(), dir.x, dir.y, dir.z);
            user.getEntityWorld().addEntity(shinsuEntity);
        }
    }

    @Override
    public int getCooldown() {
        return COOLDOWN;
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.Builder<ShinsuBlast> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public ShinsuBlast build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return new ShinsuBlast(user, level, dir);
        }

        @Override
        public boolean canCast(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return ShinsuTechnique.Builder.super.canCast(user, level, target, dir) && (!(user instanceof MobEntity) || ((MobEntity) user).getAttackTarget() != null);
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

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.SHINSU_BLAST;
        }
    }
}

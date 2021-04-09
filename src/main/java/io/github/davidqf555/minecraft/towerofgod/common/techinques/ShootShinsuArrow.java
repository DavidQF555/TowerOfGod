package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class ShootShinsuArrow extends ShinsuTechniqueInstance.Direction {

    private static final int DURATION = 200;
    private UUID arrow;

    public ShootShinsuArrow(LivingEntity user, int level, Vector3d dir) {
        super(ShinsuTechnique.SHOOT_SHINSU_ARROW, user, level, dir, DURATION);
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = RegistryHandler.SHINSU_ARROW_ENTITY.get().create(world);
            if (arrow != null) {
                ShinsuQuality quality = IShinsuStats.get(user).getQuality();
                arrow.setQuality(quality);
                arrow.setTechnique(getID());
                float speed = BowItem.getArrowVelocity(getLevel()) * (float) quality.getSpeed();
                Vector3d dir = getDirection();
                arrow.shoot(dir.x, dir.y, dir.z, speed * (float) IShinsuStats.get(user).getTension(world) * 3, 1);
                Vector3d motion = user.getMotion();
                arrow.setMotion(arrow.getMotion().add(motion.x, user.isOnGround() ? 0 : motion.y, motion.z));
                arrow.setShooter(user);
                arrow.setPosition(user.getPosX(), user.getPosYEye() - 0.1, user.getPosZ());
                this.arrow = arrow.getUniqueID();
                world.addEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public void tick(ServerWorld world) {
        if (world.getEntityByUuid(arrow) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUniqueId("Arrow", arrow);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        arrow = nbt.getUniqueId("Arrow");
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.Builder<ShootShinsuArrow> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public ShootShinsuArrow build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return new ShootShinsuArrow(user, level, dir);
        }

        @Override
        public boolean canCast(ShinsuTechnique technique, LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return ShinsuTechnique.Builder.super.canCast(technique, user, level, target, dir) && (!(user instanceof MobEntity) || ((MobEntity) user).getAttackTarget() != null);
        }

        @Nonnull
        @Override
        public ShootShinsuArrow emptyBuild() {
            return new ShootShinsuArrow(null, 0, Vector3d.ZERO);
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

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class ShinsuBlast extends ShinsuTechniqueInstance.Direction {

    private static final double BASE_SPEED = 0.5;
    private static final int DURATION = 400;
    private static final int COOLDOWN = 40;
    private UUID blast;

    public ShinsuBlast(LivingEntity user, int level, Vector3d dir) {
        super(ShinsuTechnique.SHINSU_BLAST, user, level, dir.normalize(), DURATION);
        blast = null;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity u = getUser(world);
        if (u instanceof LivingEntity) {
            ShinsuEntity shinsu = RegistryHandler.SHINSU_ENTITY.get().create(world);
            if (shinsu != null) {
                LivingEntity user = (LivingEntity) u;
                IShinsuStats stats = IShinsuStats.get(u);
                ShinsuQuality quality = stats.getQuality();
                shinsu.setShooter(user);
                shinsu.setQuality(quality);
                shinsu.setTechnique(this);
                shinsu.setLevel(getLevel());
                shinsu.setPosition(user.getPosX(), user.getPosYEye() - shinsu.getBoundingBox().getYSize() / 2, user.getPosZ());
                Vector3d dir = getDirection();
                shinsu.shoot(dir.getX(), dir.getY(), dir.getZ(), (float) (BASE_SPEED * quality.getSpeed()), 0);
                blast = shinsu.getUniqueID();
                world.addEntity(shinsu);
            }
        }
    }

    @Override
    public void tick(ServerWorld world) {
        if (blast == null || world.getEntityByUuid(blast) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (blast != null) {
            nbt.putUniqueId("Blast", blast);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Blast", Constants.NBT.TAG_INT_ARRAY)) {
            blast = nbt.getUniqueId("Blast");
        }
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

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

    public ShinsuBlast(LivingEntity user, String settings, int level, Vector3d dir) {
        super(settings, user, level, dir.normalize(), DURATION);
        blast = null;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.SHINSU_BLAST;
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
        super.onUse(world);
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
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
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
    public static class Builder implements ShinsuTechnique.IBuilder<ShinsuBlast> {

        @Override
        public ShinsuBlast build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return new ShinsuBlast(user, settings, level, dir);
        }

        @Nonnull
        @Override
        public ShinsuBlast emptyBuild() {
            return new ShinsuBlast(null, null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.SHINSU_BLAST;
        }
    }
}

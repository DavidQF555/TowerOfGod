package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.entities.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class UseLighthouseTechnique extends ShinsuTechniqueInstance.Direction {

    private UUID lighthouse;

    public UseLighthouseTechnique(LivingEntity user, int level, Vector3d dir) {
        super(ShinsuTechnique.USE_LIGHTHOUSE, user, level, dir, 1);
        lighthouse = null;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        Vector3d eye = user.getEyePosition(1);
        Vector3d change = getDirection().normalize().mul(4, 4, 4);
        while (world.getBlockState(new BlockPos(eye.add(change))).isSolid() && change.lengthSquared() > 0.625) {
            change = change.mul(0.9, 0.9, 0.9);
        }
        FlyingDevice dev = RegistryHandler.LIGHTHOUSE_ENTITY.get().create(world);
        if (dev != null && dev.canSpawn(world, SpawnReason.MOB_SUMMONED)) {
            Vector3d spawn = eye.add(change);
            dev.setPosition(spawn.x, spawn.y, spawn.z);
            dev.setOwnerID(user.getUniqueID());
            dev.setTechnique(getID());
            lighthouse = dev.getUniqueID();
            world.addEntity(dev);
        }
        super.onUse(world);
    }

    @Override
    public void tick(ServerWorld world) {
        if (world.getEntityByUuid(lighthouse) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUniqueId("Lighthouse", lighthouse);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        lighthouse = nbt.getUniqueId("Lighthouse");
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.Builder<UseLighthouseTechnique> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public UseLighthouseTechnique build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return new UseLighthouseTechnique(user, level, dir);
        }

        @Nonnull
        @Override
        public UseLighthouseTechnique emptyBuild() {
            return new UseLighthouseTechnique(null, 0, Vector3d.ZERO);
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
            return ShinsuTechnique.USE_LIGHTHOUSE;
        }
    }
}

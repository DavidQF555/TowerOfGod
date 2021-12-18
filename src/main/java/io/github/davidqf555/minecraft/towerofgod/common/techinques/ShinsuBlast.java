package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class ShinsuBlast extends ShinsuTechniqueInstance.Direction {

    private static final double BASE_SPEED = 0.5;
    private UUID blast;

    public ShinsuBlast(LivingEntity user, int level, Vector3d dir) {
        super(user, level, dir);
        blast = null;
    }

    @Override
    public int getInitialDuration() {
        return 400;
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
                ShinsuStats stats = ShinsuStats.get(u);
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
    public void periodicTick(ServerWorld world, int period) {
        if (blast == null || world.getEntityByUuid(blast) == null) {
            remove(world);
        }
        super.periodicTick(world, period);
    }

    @Override
    public int getCooldown() {
        return 40;
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

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ShinsuBlast> {

        @Override
        public Either<ShinsuBlast, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ShinsuBlast(user, level, dir));
        }

        @Override
        public ShinsuBlast emptyBuild() {
            return new ShinsuBlast(null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.SHINSU_BLAST;
        }
    }
}

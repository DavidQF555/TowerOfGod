package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
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

public class ShinsuBlast extends ShinsuTechniqueInstance {

    private static final float BASE_SPEED = 0.5f;
    private UUID blast;
    private Vector3d direction;

    public ShinsuBlast(LivingEntity user, Vector3d direction) {
        super(user);
        this.direction = direction;
        blast = null;
    }

    @Override
    public int getDuration() {
        return 400;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.SHINSU_BLAST.get();
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity u = getUser(world);
        if (u instanceof LivingEntity) {
            ShinsuEntity shinsu = EntityRegistry.SHINSU.get().create(world);
            if (shinsu != null) {
                LivingEntity user = (LivingEntity) u;
                ShinsuStats stats = ShinsuStats.get(u);
                ShinsuQuality quality = stats.getQuality();
                shinsu.setOwner(user);
                shinsu.setQuality(quality);
                shinsu.setTechnique(this);
                shinsu.setPos(user.getX(), user.getEyeY() - shinsu.getBoundingBox().getYsize() / 2, user.getZ());
                float speed = BASE_SPEED;
                if (quality != null) {
                    speed *= quality.getSpeed();
                }
                shinsu.shoot(direction.x(), direction.y(), direction.z(), speed, 0);
                blast = shinsu.getUUID();
                world.addFreshEntity(shinsu);
            }
        }
        super.onUse(world);
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        if (blast == null || world.getEntity(blast) == null) {
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
            nbt.putUUID("Blast", blast);
        }
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Blast", Constants.NBT.TAG_INT_ARRAY)) {
            blast = nbt.getUUID("Blast");
        }
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ShinsuBlast> {

        @Override
        public Either<ShinsuBlast, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ShinsuBlast(user, dir));
        }

        @Override
        public ShinsuBlast blankCreate() {
            return new ShinsuBlast(null, Vector3d.ZERO);
        }

    }
}

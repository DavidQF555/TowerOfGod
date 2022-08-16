package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
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

public class ShootShinsuArrow extends ShinsuTechniqueInstance {

    private Vector3d direction;
    private float velocity;
    private UUID arrow;

    public ShootShinsuArrow(LivingEntity user, Vector3d direction) {
        super(user);
        this.direction = direction;
        arrow = null;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public int getDuration() {
        return 200;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.SHOOT_SHINSU_ARROW;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(world);
            if (arrow != null) {
                ShinsuQuality quality = ShinsuStats.get(user).getQuality();
                arrow.setQuality(quality);
                arrow.setTechnique(getID());
                float speed = velocity * 3;
                if (quality != null) {
                    speed *= quality.getSpeed();
                }
                arrow.shoot(direction.getX(), direction.getY(), direction.getZ(), speed, 1);
                arrow.setShooter(user);
                arrow.setPosition(user.getPosX(), user.getPosYEye() - 0.1, user.getPosZ());
                this.arrow = arrow.getUniqueID();
                world.addEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        if (arrow == null || world.getEntityByUuid(arrow) == null) {
            remove(world);
        }
        super.periodicTick(world, period);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (arrow != null) {
            nbt.putUniqueId("Arrow", arrow);
        }
        nbt.putFloat("Velocity", velocity);
        nbt.putDouble("X", direction.getX());
        nbt.putDouble("Y", direction.getY());
        nbt.putDouble("Z", direction.getZ());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Arrow", Constants.NBT.TAG_INT_ARRAY)) {
            arrow = nbt.getUniqueId("Arrow");
        }
        if (nbt.contains("Velocity", Constants.NBT.TAG_FLOAT)) {
            setVelocity(nbt.getFloat("Velocity"));
        }
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ShootShinsuArrow> {

        @Override
        public Either<ShootShinsuArrow, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ShootShinsuArrow(user, dir));
        }

        @Override
        public ShootShinsuArrow blankCreate() {
            return new ShootShinsuArrow(null, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.SHOOT_SHINSU_ARROW;
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[0];
        }
    }
}

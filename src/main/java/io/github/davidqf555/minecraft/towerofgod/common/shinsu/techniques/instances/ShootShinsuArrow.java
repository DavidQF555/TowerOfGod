package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
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

    public ShootShinsuArrow(Entity user, Vector3d direction) {
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
        return ShinsuTechniqueRegistry.SHOOT_SHINSU_ARROW.get();
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(world);
            if (arrow != null) {
                ShinsuAttribute attribute = ShinsuQualityData.get(user).getAttribute();
                arrow.setAttribute(attribute);
                arrow.setTechnique(getID());
                arrow.shoot(direction.x(), direction.y(), direction.z(), velocity, 1);
                arrow.setOwner(user);
                arrow.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
                this.arrow = arrow.getUUID();
                world.addFreshEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 5;
    }

    @Override
    public void tick(ServerWorld world) {
        if (arrow == null || world.getEntity(arrow) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (arrow != null) {
            nbt.putUUID("Arrow", arrow);
        }
        nbt.putFloat("Velocity", velocity);
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Arrow", Constants.NBT.TAG_INT_ARRAY)) {
            arrow = nbt.getUUID("Arrow");
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
        public Either<ShootShinsuArrow, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ShootShinsuArrow(user, dir));
        }

        @Override
        public ShootShinsuArrow blankCreate() {
            return new ShootShinsuArrow(null, Vector3d.ZERO);
        }

    }
}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuSpearEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class ThrowSpear extends ShinsuTechniqueInstance {

    private Vector3d direction;
    private UUID spear;

    public ThrowSpear(Entity user, Vector3d direction) {
        super(user);
        this.direction = direction;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.THROW_SPEAR.get();
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getDuration() {
        return 200;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        ShinsuSpearEntity proj = EntityRegistry.SHINSU_SPEAR.get().create(world);
        if (proj != null) {
            proj.setOwner(user);
            float speed = 2.5f;
            ShinsuAttribute attribute = ShinsuQualityData.get(user).getAttribute();
            if (attribute != null) {
                speed *= attribute.getSpeed();
            }
            proj.shoot(direction.x(), direction.y(), direction.z(), speed, 1);
            proj.setPos(user.getX(), user.getEyeY(), user.getZ());
            proj.setAttribute(attribute);
            proj.setTechnique(getID());
            user.level.addFreshEntity(proj);
            user.level.playSound(null, proj, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1, 1);
            spear = proj.getUUID();
        }
        super.onUse(world);
    }


    @Override
    public void tick(ServerWorld world) {
        if (spear == null || world.getEntity(spear) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (spear != null) {
            nbt.putUUID("Spear", spear);
        }
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Spear", Constants.NBT.TAG_INT_ARRAY)) {
            spear = nbt.getUUID("Spear");
        }
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }


    public static class Factory implements ShinsuTechnique.IFactory<ThrowSpear> {

        @Override
        public Either<ThrowSpear, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ThrowSpear(user, dir));
        }

        @Override
        public ThrowSpear blankCreate() {
            return new ThrowSpear(null, Vector3d.ZERO);
        }

    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Random;

public class Flamethrower extends ShinsuTechniqueInstance {

    private int duration, count;
    private float spread;
    private double magnitude;

    public Flamethrower(LivingEntity user, int duration, float spread, int count, double magnitude) {
        super(user);
        this.duration = duration;
        this.spread = spread;
        this.count = count;
        this.magnitude = magnitude;
    }

    @Override
    public void tick(ServerWorld world) {
        Entity user = getUser(world);
        Random rand = world.getRandom();
        Vector3d center = user.getLookAngle();
        for (int i = 0; i < count; i++) {
            SmallFireballEntity fire = EntityType.SMALL_FIREBALL.create(world);
            if (fire != null) {
                float angle = spread * i / count - spread / 2;
                Vector3d dir = center.yRot(angle * (float) Math.PI / 180).add(rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1);
                fire.setPos(user.getX(), user.getEyeY(), user.getZ());
                fire.setDeltaMovement(dir.scale(magnitude));
                fire.yPower = -0.05;
                fire.setOwner(user);
                world.addFreshEntity(fire);
            }
        }
        super.tick(world);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FLAMETHROWER.get();
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putInt("Duration", duration);
        tag.putInt("Count", count);
        tag.putFloat("Spread", spread);
        tag.putDouble("Magnitude", magnitude);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Count", Constants.NBT.TAG_INT)) {
            count = nbt.getInt("Count");
        }
        if (nbt.contains("Spread", Constants.NBT.TAG_FLOAT)) {
            spread = nbt.getFloat("Spread");
        }
        if (nbt.contains("Magnitude", Constants.NBT.TAG_DOUBLE)) {
            magnitude = nbt.getDouble("Magnitude");
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Flamethrower> {

        @Override
        public Either<Flamethrower, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new Flamethrower(user, 60, 60, 7, 1));
        }

        @Override
        public Flamethrower blankCreate() {
            return new Flamethrower(null, 0, 0, 0, 0);
        }
    }
}

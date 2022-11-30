package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlackFish extends ShinsuTechniqueInstance {

    private int duration, light;

    public BlackFish(Entity user, int duration, int light) {
        super(user);
        this.duration = duration;
        this.light = light;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void tick(ServerLevel world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity && world.getLightEmission(e.blockPosition()) <= light) {
            ((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, true, true, true));
        }
        super.tick(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.BLACK_FISH.get();
    }

    @Override
    public int getCooldown() {
        return 400;
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Light", Tag.TAG_INT)) {
            light = nbt.getInt("Light");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("Duration", duration);
        nbt.putInt("Light", light);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BlackFish> {

        @Override
        public Either<BlackFish, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new BlackFish(user, 2400, 7));
        }

        @Override
        public BlackFish blankCreate() {
            return new BlackFish(null, 0, 0);
        }

    }
}

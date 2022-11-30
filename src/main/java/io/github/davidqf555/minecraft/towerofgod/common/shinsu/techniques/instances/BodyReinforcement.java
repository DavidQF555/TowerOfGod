package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BodyReinforcement extends ShinsuTechniqueInstance {

    private int duration;

    public BodyReinforcement(Entity user, int duration) {
        super(user);
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.BODY_REINFORCEMENT.get();
    }

    @Override
    public void tick(ServerLevel world) {
        Entity user = getUser(world);
        if (user instanceof LivingEntity) {
            ShinsuStats stats = ShinsuStats.get(user);
            int amp = (int) (stats.getTension() * stats.getResistance()) / 2;
            ((LivingEntity) user).addEffect(new MobEffectInstance(EffectRegistry.BODY_REINFORCEMENT.get(), 2, amp, false, true, true));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BodyReinforcement> {

        @Override
        public Either<BodyReinforcement, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new BodyReinforcement(user, 2400));
        }

        @Override
        public BodyReinforcement blankCreate() {
            return new BodyReinforcement(null, 0);
        }

    }
}

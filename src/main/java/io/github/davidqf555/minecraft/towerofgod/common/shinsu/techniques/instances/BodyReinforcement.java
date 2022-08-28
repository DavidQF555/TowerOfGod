package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
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

    private int duration, level;

    public BodyReinforcement(LivingEntity user, int duration, int level) {
        super(user);
        this.duration = duration;
        this.level = level;
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
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            int amp = (int) (level * ShinsuStats.get(user).getRawTension());
            user.addEffect(new MobEffectInstance(EffectRegistry.BODY_REINFORCEMENT.get(), 2, amp - 1, false, true, true));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getDuration() + 150;
    }

    @Override
    public int getShinsuUse() {
        return level + 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Level", Tag.TAG_INT)) {
            level = nbt.getInt("Level");
        }
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("Level", level);
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BodyReinforcement> {

        @Override
        public Either<BodyReinforcement, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new BodyReinforcement(user, 200 + level * 100, level));
        }

        @Override
        public BodyReinforcement blankCreate() {
            return new BodyReinforcement(null, 0, 0);
        }

    }
}

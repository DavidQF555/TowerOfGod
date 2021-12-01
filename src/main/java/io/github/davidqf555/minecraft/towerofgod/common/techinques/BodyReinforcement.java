package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BodyReinforcement extends ShinsuTechniqueInstance {

    public BodyReinforcement(LivingEntity user, int level) {
        super(user, level);
    }

    @Override
    public int getInitialDuration() {
        return 200 + getLevel() * 100;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.BODY_REINFORCEMENT;
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            int level = (int) (getLevel() * ShinsuStats.get(user).getTension(world));
            user.addPotionEffect(new EffectInstance(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get(), Math.min(period, ticksLeft()) + 1, level - 1, false, true, true));
        }
        super.periodicTick(world, period);
    }

    @Override
    public int getCooldown() {
        return getInitialDuration() + 150;
    }

    @Override
    public int getShinsuUse() {
        return getLevel() + 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<BodyReinforcement> {

        @Override
        public Either<BodyReinforcement, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return Either.left(new BodyReinforcement(user, level));
        }

        @Override
        public BodyReinforcement emptyBuild() {
            return new BodyReinforcement(null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.BODY_REINFORCEMENT;
        }
    }
}

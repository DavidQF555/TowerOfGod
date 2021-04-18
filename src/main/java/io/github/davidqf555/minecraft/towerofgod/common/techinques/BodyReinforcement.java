package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BodyReinforcement extends ShinsuTechniqueInstance {

    private static final int BASE_DURATION = 300;

    public BodyReinforcement(LivingEntity user, int level) {
        super(ShinsuTechnique.BODY_REINFORCEMENT, user, level, level * BASE_DURATION);
    }

    @Override
    public void tick(ServerWorld world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            int level = (int) (getLevel() * IShinsuStats.get(user).getTension(world));
            user.addPotionEffect(new EffectInstance(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get(), 2, level - 1, true, false, false));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getLevel() * BASE_DURATION;
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.Builder<BodyReinforcement> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public BodyReinforcement build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return new BodyReinforcement(user, level);
        }

        @Nonnull
        @Override
        public BodyReinforcement emptyBuild() {
            return new BodyReinforcement(null, 0);
        }

        @Override
        public int getShinsuUse() {
            return shinsu;
        }

        @Override
        public int getBaangUse() {
            return baangs;
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.BODY_REINFORCEMENT;
        }
    }
}

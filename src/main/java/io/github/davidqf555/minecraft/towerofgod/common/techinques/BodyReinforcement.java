package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
            user.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 2, level / 2, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.SPEED, 2, level, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.HASTE, 2, level, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.STRENGTH, 2, level / 3, true, false, false));
            user.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 2, level / 2, true, false, false));
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
    }
}

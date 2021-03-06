package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlackFish extends ShinsuTechniqueInstance {

    private static final int BASE_DURATION = 600;

    public BlackFish(LivingEntity user, int level) {
        super(ShinsuTechnique.BLACK_FISH, user, level, level * BASE_DURATION);
    }

    @Override
    public void tick(ServerWorld world) {
        Entity e = getUser(world);
        int level = getLevel();
        if (e instanceof LivingEntity && world.getLight(e.getPosition()) <= level * 5) {
            ((LivingEntity) e).addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 2, 1, true, true, true));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getLevel() * BASE_DURATION;
    }

    public static class Builder implements ShinsuTechnique.Builder<BlackFish> {

        private final int shinsu;
        private final int baangs;

        public Builder(int shinsu, int baangs) {
            this.shinsu = shinsu;
            this.baangs = baangs;
        }

        @Override
        public BlackFish build(@Nonnull LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            return new BlackFish(user, level);
        }

        @Nonnull
        @Override
        public BlackFish emptyBuild() {
            return new BlackFish(null, 0);
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

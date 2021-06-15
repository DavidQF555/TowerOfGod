package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlackFish extends ShinsuTechniqueInstance {

    private static final int BASE_DURATION = 600;

    public BlackFish(LivingEntity user, int level) {
        super(null, user, level, level * BASE_DURATION);
    }

    @Override
    public void tick(ServerWorld world) {
        Entity e = getUser(world);
        int level = getLevel();
        if (e instanceof LivingEntity && world.getLight(e.getPosition()) <= level * 5) {
            ((LivingEntity) e).addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 2, 0, true, true, true));
        }
        super.tick(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.BLACK_FISH;
    }

    @Override
    public int getCooldown() {
        return getLevel() * BASE_DURATION;
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<BlackFish> {

        @Override
        public BlackFish build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return new BlackFish(user, level);
        }

        @Nonnull
        @Override
        public BlackFish emptyBuild() {
            return new BlackFish(null, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.BLACK_FISH;
        }
    }
}

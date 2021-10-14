package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlackFish extends ShinsuTechniqueInstance {

    public BlackFish(LivingEntity user, int level) {
        super(null, user, level);
    }

    @Override
    public int getInitialDuration() {
        return 100 + getLevel() * 40;
    }

    @Override
    public void tick(ServerWorld world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity && world.getLight(e.getPosition()) <= getLevel()) {
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
        return getInitialDuration() + 40;
    }

    @Override
    public int getShinsuUse() {
        return getLevel() * 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<BlackFish> {

        @Override
        public BlackFish build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return new BlackFish(user, level);
        }

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

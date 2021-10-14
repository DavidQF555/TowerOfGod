package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ReverseFlowControl extends ShinsuTechniqueInstance.Targetable {

    private static final double RANGE = 3;

    public ReverseFlowControl(LivingEntity user, int level, LivingEntity target) {
        super(null, user, level, target);
    }

    @Override
    public int getInitialDuration() {
        return 20 + getLevel() * 10;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.REVERSE_FLOW_CONTROL;
    }

    @Override
    public void tick(ServerWorld world) {
        Entity user = getUser(world);
        Entity target = getTarget(world);
        if (user != null && target instanceof LivingEntity) {
            if (user.getDistanceSq(target) > RANGE * RANGE) {
                remove(world);
                return;
            }
            double resistance = ShinsuStats.getNetResistance(world, user, target);
            int level = (int) (resistance * getLevel());
            ((LivingEntity) target).addPotionEffect(new EffectInstance(RegistryHandler.REVERSE_FLOW_EFFECT.get(), 2, level - 1));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getInitialDuration() + 500;
    }

    @Override
    public int getShinsuUse() {
        return 5 + getLevel() * 5;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @MethodsReturnNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<ReverseFlowControl> {

        @Override
        public ReverseFlowControl build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            return target instanceof LivingEntity && user.getDistanceSq(target) <= RANGE * RANGE ? new ReverseFlowControl(user, level, (LivingEntity) target) : null;
        }

        @Override
        public ReverseFlowControl emptyBuild() {
            return new ReverseFlowControl(null, 0, null);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.REVERSE_FLOW_CONTROL;
        }
    }
}

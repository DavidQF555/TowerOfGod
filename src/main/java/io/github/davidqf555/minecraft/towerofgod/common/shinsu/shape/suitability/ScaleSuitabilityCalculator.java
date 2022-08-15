package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.suitability;

import net.minecraft.entity.player.ServerPlayerEntity;

public class ScaleSuitabilityCalculator implements SuitabilityCalculator {

    private final SuitabilityCalculator original;
    private final double factor;

    public ScaleSuitabilityCalculator(SuitabilityCalculator original, double factor) {
        this.original = original;
        this.factor = factor;
    }

    @Override
    public double calculate(ServerPlayerEntity player) {
        return original.calculate(player) * factor;
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.suitability;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface SuitabilityCalculator {

    static SumSuitabilityCalculator sum(SuitabilityCalculator... all) {
        return new SumSuitabilityCalculator(all);
    }

    double calculate(ServerPlayerEntity player);

    default ScaleSuitabilityCalculator scale(double scale) {
        return new ScaleSuitabilityCalculator(this, scale);
    }

}

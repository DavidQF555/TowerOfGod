package io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.stream.Stream;

public interface SuitabilityCalculator {

    static SumSuitabilityCalculator sum(SuitabilityCalculator... all) {
        return new SumSuitabilityCalculator(all);
    }

    static SumSuitabilityCalculator sum(Stream<SuitabilityCalculator> all) {
        return sum(all.toArray(SuitabilityCalculator[]::new));
    }

    double calculate(ServerPlayerEntity player);

    default ScaleSuitabilityCalculator scale(double scale) {
        return new ScaleSuitabilityCalculator(this, scale);
    }

}

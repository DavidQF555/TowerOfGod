package io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Arrays;

public class SumSuitabilityCalculator implements SuitabilityCalculator {

    private final SuitabilityCalculator[] all;

    public SumSuitabilityCalculator(SuitabilityCalculator... all) {
        this.all = all;
    }

    @Override
    public double calculate(ServerPlayerEntity player) {
        return Arrays.stream(all).mapToDouble(calculator -> calculator.calculate(player)).sum();
    }
}

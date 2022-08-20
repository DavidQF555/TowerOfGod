package io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stat;

public class StatSuitabilityCalculator implements SuitabilityCalculator {

    private final Stat<?> stat;

    public StatSuitabilityCalculator(Stat<?> stat) {
        this.stat = stat;
    }

    @Override
    public double calculate(ServerPlayerEntity player) {
        return player.getStats().getValue(stat);
    }

}

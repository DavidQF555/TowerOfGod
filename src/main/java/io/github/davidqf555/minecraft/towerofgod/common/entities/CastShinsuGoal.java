package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class CastShinsuGoal<T extends Mob & IShinsuUser> extends Goal {

    private final T entity;
    private final int cooldown;
    private int time;
    private ShinsuTechnique selected;
    private int duration;

    public CastShinsuGoal(T entity, int cooldown) {
        this.entity = entity;
        this.cooldown = cooldown;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (--time <= 0) {
            List<ShinsuTechnique> possible = new ArrayList<>();
            for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
                if (technique.shouldMobUse(entity)) {
                    create(technique).ifPresent(inst -> possible.add(technique));
                }
            }
            if (possible.isEmpty()) {
                return false;
            }
            selected = selectTechnique(possible);
            return true;
        }
        return false;
    }

    protected ShinsuTechnique selectTechnique(List<ShinsuTechnique> possible) {
        return possible.get(entity.getRandom().nextInt(possible.size()));
    }

    @Override
    public boolean canContinueToUse() {
        return duration > 0 && create(selected).isPresent();
    }

    @Override
    public void tick() {
        if (--duration <= 0) {
            selected.cast(entity, entity.getTarget(), entity.getLookAngle());
        }
    }

    private Optional<? extends ShinsuTechniqueInstance> create(ShinsuTechnique technique) {
        return technique.create(entity, entity.getTarget(), entity.getLookAngle()).left();
    }

    @Override
    public void start() {
        duration = selected.getCombination().size() * 15;
        entity.setCasting(true);
    }

    @Override
    public void stop() {
        entity.setCasting(false);
        selected = null;
        duration = 0;
        time = cooldown;
    }
}
package io.github.davidqf555.minecraft.towerofgod.common.entities;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CastShinsuGoal<T extends Mob & IShinsuUser<T>> extends Goal {

    private final T entity;
    private final int cooldown;
    private final int cast;
    private int time;
    private Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity> selected;
    private int duration;

    public CastShinsuGoal(T entity, int cast, int cooldown) {
        this.entity = entity;
        this.cast = cast;
        this.cooldown = cooldown;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (--time <= 0) {
            List<Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity>> possible = new ArrayList<>();
            for (BaangEntity baang : entity.getShinsuTechniqueData().getBaangs((ServerLevel) entity.level)) {
                ConfiguredShinsuTechniqueType<?, ?> technique = baang.getTechniqueType();
                if (technique != null && technique.getConfig().getMobCondition().shouldUse(entity)) {
                    possible.add(Pair.of(technique, baang));
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

    protected Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity> selectTechnique(List<Pair<ConfiguredShinsuTechniqueType<?, ?>, BaangEntity>> possible) {
        return possible.get(entity.getRandom().nextInt(possible.size()));
    }

    @Override
    public boolean canContinueToUse() {
        return duration > 0 && selected.getSecond().isAlive() && selected.getFirst().getConfig().getMobCondition().shouldUse(entity);
    }

    @Override
    public void tick() {
        if (--duration <= 0) {
            ShinsuTechniqueInstance<?, ?> inst = selected.getFirst().cast(entity, entity.getTarget());
            if (inst != null) {
                selected.getSecond().setTechniqueID(inst.getData().id);
            }
        }
    }

    @Override
    public void start() {
        duration = cast;
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
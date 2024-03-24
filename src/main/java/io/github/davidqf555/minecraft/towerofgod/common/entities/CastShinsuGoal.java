package io.github.davidqf555.minecraft.towerofgod.common.entities;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
    private Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, BaangEntity> selected;
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
            List<Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, BaangEntity>> possible = new ArrayList<>();
            for (BaangEntity baang : entity.getShinsuTechniqueData().getBaangs((ServerLevel) entity.level)) {
                possible.add(Pair.of(baang.getTechniqueTypeKey(), baang));
            }
            if (possible.isEmpty()) {
                return false;
            }
            selected = selectTechnique(possible);
            return true;
        }
        return false;
    }

    protected Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, BaangEntity> selectTechnique(List<Pair<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, BaangEntity>> possible) {
        return possible.get(entity.getRandom().nextInt(possible.size()));
    }

    @Override
    public boolean canContinueToUse() {
        return duration > 0 && selected.getSecond().isAlive();
    }

    @Override
    public void tick() {
        if (--duration <= 0) {
            Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(entity.getServer().registryAccess());
            ShinsuTechniqueInstance<?, ?> inst = registry.getOrThrow(selected.getFirst()).cast(entity, entity.getTarget());
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
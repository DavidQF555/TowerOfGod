package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ToggleableShinsuTechnique extends ShinsuTechnique {

    public ToggleableShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, List<Direction> combination) {
        super(indefinite, factory, icon, requirements, combination);
    }

    @Override
    public Either<? extends ShinsuTechniqueInstance, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
        if (ShinsuStats.get(user).getTechniques().stream().anyMatch(inst -> equals(inst.getTechnique()))) {
            return Either.left(getFactory().blankCreate());
        }
        return super.create(user, target, dir);
    }

    @Override
    public void cast(LivingEntity user, @Nullable Entity target, Vec3 dir) {
        if (user.level instanceof ServerLevel) {
            ShinsuStats stats = ShinsuStats.get(user);
            Optional<ShinsuTechniqueInstance> used = stats.getTechniques().stream().filter(instance -> equals(instance.getTechnique())).findAny();
            if (used.isPresent()) {
                used.get().remove((ServerLevel) user.level);
                return;
            }
        }
        super.cast(user, target, dir);
    }

}

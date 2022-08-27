package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ToggleableShinsuTechnique extends ShinsuTechnique {

    public ToggleableShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, List<Direction> combination) {
        super(indefinite, factory, icon, requirements, combination);
    }

    @Override
    public Either<? extends ShinsuTechniqueInstance, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
        if (ShinsuStats.get(user).getTechniques().stream().anyMatch(inst -> equals(inst.getTechnique()))) {
            return Either.left(getFactory().blankCreate());
        }
        return super.create(user, target, dir);
    }

    @Override
    public void cast(LivingEntity user, @Nullable Entity target, Vector3d dir) {
        if (user.level instanceof ServerWorld) {
            ShinsuStats stats = ShinsuStats.get(user);
            Optional<ShinsuTechniqueInstance> used = stats.getTechniques().stream().filter(instance -> equals(instance.getTechnique())).findAny();
            if (used.isPresent()) {
                used.get().remove((ServerWorld) user.level);
                return;
            }
        }
        super.cast(user, target, dir);
    }

}

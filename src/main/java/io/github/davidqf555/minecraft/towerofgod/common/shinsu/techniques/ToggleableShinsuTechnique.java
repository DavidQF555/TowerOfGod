package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.Stop;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class ToggleableShinsuTechnique extends ShinsuTechnique {

    public ToggleableShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, @Nullable UsageData usage, MobUseCondition mobUseCondition) {
        super(indefinite, factory, icon, requirements, usage, mobUseCondition);
    }

    @Override
    public Either<? extends ShinsuTechniqueInstance, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
        if (getExistingInstance(user).isPresent()) {
            return Either.left(new Stop(user, this));
        }
        return super.create(user, target, dir);
    }

    @Override
    public void cast(Entity user, @Nullable Entity target, Vec3 dir) {
        Optional<ShinsuTechniqueInstance> used = getExistingInstance(user);
        if (used.isPresent()) {
            used.get().remove((ServerLevel) user.level);
            return;
        }
        super.cast(user, target, dir);
    }

    private Optional<ShinsuTechniqueInstance> getExistingInstance(Entity entity) {
        for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(entity).getTechniques()) {
            if (equals(inst.getTechnique())) {
                return Optional.of(inst);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean shouldMobUse(Mob mob) {
        return super.shouldMobUse(mob) && !getExistingInstance(mob).isPresent();
    }

}

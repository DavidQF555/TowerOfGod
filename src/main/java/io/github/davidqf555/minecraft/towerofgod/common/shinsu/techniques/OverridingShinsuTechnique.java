package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;

public class OverridingShinsuTechnique extends ShinsuTechnique {

    public OverridingShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, List<Direction> combination, MobUseCondition mobUseCondition) {
        super(indefinite, factory, icon, requirements, combination, mobUseCondition);
    }

    @Override
    protected int getNetShinsuUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetShinsuUse(user, instance);
        Optional<ShinsuTechniqueInstance> inst = getExistingInstance(user);
        if (inst.isPresent()) {
            def -= inst.get().getShinsuUse();
        }
        return def;
    }

    @Override
    protected int getNetBaangsUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetBaangsUse(user, instance);
        Optional<ShinsuTechniqueInstance> inst = getExistingInstance(user);
        if (inst.isPresent()) {
            def -= inst.get().getBaangsUse();
        }
        return def;
    }

    @Override
    public void cast(LivingEntity user, ShinsuTechniqueInstance instance) {
        if (user.level instanceof ServerWorld) {
            getExistingInstance(user).ifPresent(inst -> inst.remove((ServerWorld) user.level));
        }
        super.cast(user, instance);
    }

    private Optional<ShinsuTechniqueInstance> getExistingInstance(LivingEntity entity) {
        for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(entity).getTechniques()) {
            if (equals(inst.getTechnique())) {
                return Optional.of(inst);
            }
        }
        return Optional.empty();
    }
}

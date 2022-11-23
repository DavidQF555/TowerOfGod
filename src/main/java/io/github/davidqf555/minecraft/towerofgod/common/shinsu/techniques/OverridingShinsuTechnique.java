package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;

public class OverridingShinsuTechnique extends ShinsuTechnique {

    public OverridingShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, @Nullable UsageData usage, MobUseCondition mobUseCondition) {
        super(indefinite, factory, icon, requirements, usage, mobUseCondition);
    }

    @Override
    public int getNetShinsuUse(Entity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetShinsuUse(user, instance);
        Optional<ShinsuTechniqueInstance> inst = getExistingInstance(user);
        if (inst.isPresent()) {
            def -= inst.get().getShinsuUse();
        }
        return def;
    }

    @Override
    public int getNetBaangsUse(Entity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetBaangsUse(user, instance);
        Optional<ShinsuTechniqueInstance> inst = getExistingInstance(user);
        if (inst.isPresent()) {
            def -= inst.get().getBaangsUse();
        }
        return def;
    }

    @Override
    public void cast(Entity user, ShinsuTechniqueInstance instance) {
        getExistingInstance(user).ifPresent(inst -> inst.remove((ServerWorld) user.level));
        super.cast(user, instance);
    }

    private Optional<ShinsuTechniqueInstance> getExistingInstance(Entity entity) {
        for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(entity).getTechniques()) {
            if (equals(inst.getTechnique())) {
                return Optional.of(inst);
            }
        }
        return Optional.empty();
    }
}

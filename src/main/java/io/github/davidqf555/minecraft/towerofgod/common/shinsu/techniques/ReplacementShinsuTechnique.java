package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ReplacementShinsuTechnique extends ShinsuTechnique {

    private final Predicate<ShinsuTechniqueInstance> replace;

    public ReplacementShinsuTechnique(Predicate<ShinsuTechniqueInstance> replace, boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, @Nullable UsageData usage, MobUseCondition mobUseCondition) {
        super(indefinite, factory, icon, requirements, usage, mobUseCondition);
        this.replace = replace;
    }

    protected boolean shouldReplace(ShinsuTechniqueInstance inst) {
        return replace.test(inst);
    }

    protected Set<ShinsuTechniqueInstance> getReplacedInstances(Entity entity) {
        Set<ShinsuTechniqueInstance> all = new HashSet<>();
        for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(entity).getTechniques()) {
            if (shouldReplace(inst)) {
                all.add(inst);
            }
        }
        return all;
    }

    @Override
    public int getNetShinsuUse(Entity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetShinsuUse(user, instance);
        for (ShinsuTechniqueInstance inst : getReplacedInstances(user)) {
            def -= inst.getShinsuUse();
        }
        return def;
    }

    @Override
    public void cast(Entity user, ShinsuTechniqueInstance instance) {
        getReplacedInstances(user).forEach(inst -> inst.remove((ServerLevel) user.level));
        super.cast(user, instance);
    }

}

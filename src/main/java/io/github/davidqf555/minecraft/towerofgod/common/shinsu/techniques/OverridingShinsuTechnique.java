package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

public class OverridingShinsuTechnique extends ReplacementShinsuTechnique {

    public OverridingShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, @Nullable UsageData usage, MobUseCondition mobUseCondition) {
        super(inst -> false, indefinite, factory, icon, requirements, usage, mobUseCondition);
    }

    @Override
    protected boolean shouldReplace(ShinsuTechniqueInstance inst) {
        return inst.getTechnique().equals(this);
    }

    @Override
    public boolean shouldMobUse(Mob mob) {
        return super.shouldMobUse(mob) && getReplacedInstances(mob).isEmpty();
    }

}

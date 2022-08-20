package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class OverridingShinsuTechnique extends ShinsuTechnique {

    public OverridingShinsuTechnique(boolean indefinite, IFactory<?> factory, IRenderData icon, IRequirement[] requirements, List<Direction> combination) {
        super(indefinite, factory, icon, requirements, combination);
    }

    @Override
    protected int getNetShinsuUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetShinsuUse(user, instance);
        for (ShinsuTechniqueInstance inst : ShinsuStats.get(user).getTechniques()) {
            if (equals(inst.getTechnique())) {
                def -= inst.getShinsuUse();
            }
        }
        return def;
    }

    @Override
    protected int getNetBaangsUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        int def = super.getNetBaangsUse(user, instance);
        for (ShinsuTechniqueInstance inst : ShinsuStats.get(user).getTechniques()) {
            if (equals(inst.getTechnique())) {
                def -= inst.getBaangsUse();
            }
        }
        return def;
    }

    @Override
    public void cast(LivingEntity user, ShinsuTechniqueInstance instance) {
        if (user.world instanceof ServerWorld) {
            for (ShinsuTechniqueInstance inst : new ArrayList<>(ShinsuStats.get(user).getTechniques())) {
                if (equals(inst.getTechnique())) {
                    inst.remove((ServerWorld) user.world);
                }
            }
        }
        super.cast(user, instance);
    }

}

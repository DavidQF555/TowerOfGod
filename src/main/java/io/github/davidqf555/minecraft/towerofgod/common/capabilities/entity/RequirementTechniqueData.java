package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RequirementTechniqueData<T extends Entity> extends CooldownTechniqueData<T> {

    @Override
    public Optional<ITextComponent> getCastError(T user, ShinsuTechniqueInstance instance) {
        if (!isUnlocked(user, instance.getTechnique())) {
            return Optional.of(Messages.LOCKED);
        }
        return super.getCastError(user, instance);
    }

    public boolean isUnlocked(T user, ShinsuTechnique technique) {
        if (technique.isObtainable()) {
            for (IRequirement requirement : technique.getRequirements()) {
                if (!requirement.isUnlocked(user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Set<ShinsuTechnique> getUnlockedTechniques(T user) {
        Set<ShinsuTechnique> all = new HashSet<>();
        for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
            if (isUnlocked(user, technique)) {
                all.add(technique);
            }
        }
        return all;
    }

}

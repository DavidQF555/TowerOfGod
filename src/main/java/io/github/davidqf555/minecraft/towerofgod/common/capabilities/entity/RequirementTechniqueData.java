package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class RequirementTechniqueData extends CooldownTechniqueData {

    @Override
    public Optional<ITextComponent> getCastError(LivingEntity user, ShinsuTechniqueInstance instance) {
        for (IRequirement requirement : instance.getTechnique().getRequirements()) {
            if (!requirement.isUnlocked(user)) {
                return Optional.of(Messages.LOCKED);
            }
        }
        return super.getCastError(user, instance);
    }
}

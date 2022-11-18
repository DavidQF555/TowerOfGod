package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ToggleableShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Optional;

public class MobTechniqueData extends RequirementTechniqueData {

    @Override
    public Optional<ITextComponent> getCastError(LivingEntity user, ShinsuTechniqueInstance instance) {
        if (user instanceof MobEntity && (!instance.getTechnique().getMobUseCondition().shouldUse((MobEntity) user) || instance.getTechnique() instanceof ToggleableShinsuTechnique)) {
            return Optional.of(StringTextComponent.EMPTY);
        }
        return super.getCastError(user, instance);
    }

}

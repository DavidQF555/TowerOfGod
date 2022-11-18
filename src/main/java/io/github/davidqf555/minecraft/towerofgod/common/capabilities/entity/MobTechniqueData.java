package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ToggleableShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Optional;

public class MobTechniqueData extends RequirementTechniqueData<MobEntity> {

    @CapabilityInject(MobTechniqueData.class)
    public static Capability<MobTechniqueData> capability = null;

    public static MobTechniqueData get(MobEntity entity) {
        return entity.getCapability(capability).orElseGet(MobTechniqueData::new);
    }

    @Override
    public Optional<ITextComponent> getCastError(MobEntity user, ShinsuTechniqueInstance instance) {
        if (!instance.getTechnique().getMobUseCondition().shouldUse(user) || instance.getTechnique() instanceof ToggleableShinsuTechnique) {
            return Optional.of(StringTextComponent.EMPTY);
        }
        return super.getCastError(user, instance);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MobTechniqueData extends RequirementTechniqueData<MobEntity> {

    @CapabilityInject(MobTechniqueData.class)
    public static Capability<MobTechniqueData> capability = null;

    public static MobTechniqueData get(MobEntity entity) {
        return entity.getCapability(capability).orElseGet(MobTechniqueData::new);
    }

}

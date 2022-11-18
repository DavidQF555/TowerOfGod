package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.RequirementTechniqueData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PlayerTechniqueData extends RequirementTechniqueData<PlayerEntity> {

    @CapabilityInject(PlayerTechniqueData.class)
    public static Capability<PlayerTechniqueData> capability = null;

    public static PlayerTechniqueData get(PlayerEntity player) {
        return player.getCapability(capability).orElseGet(PlayerTechniqueData::new);
    }

}

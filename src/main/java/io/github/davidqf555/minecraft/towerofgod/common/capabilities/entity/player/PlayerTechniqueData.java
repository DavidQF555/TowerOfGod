package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.RequirementTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class PlayerTechniqueData extends RequirementTechniqueData<PlayerEntity> {

    @CapabilityInject(PlayerTechniqueData.class)
    public static Capability<PlayerTechniqueData> capability = null;
    private final Set<ShinsuTechnique> unlocked = new HashSet<>();

    public static PlayerTechniqueData get(PlayerEntity player) {
        return player.getCapability(capability).orElseGet(PlayerTechniqueData::new);
    }

    public Set<ShinsuTechnique> getUnlocked() {
        return unlocked;
    }

    @Override
    public boolean isUnlocked(PlayerEntity user, ShinsuTechnique technique) {
        return getUnlocked().contains(technique) && super.isUnlocked(user, technique);
    }

    public boolean unlock(ShinsuTechnique technique) {
        if (technique.isObtainable()) {
            return unlocked.add(technique);
        }
        return false;
    }

    public boolean lock(ShinsuTechnique technique) {
        return unlocked.remove(technique);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        ListNBT unlocked = new ListNBT();
        getUnlocked().forEach(technique -> {
            unlocked.add(StringNBT.valueOf(technique.getRegistryName().toString()));
        });
        tag.put("Unlocked", unlocked);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Unlocked", Constants.NBT.TAG_LIST)) {
            for (INBT tag : nbt.getList("Unlocked", Constants.NBT.TAG_STRING)) {
                unlock(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(tag.getAsString())));
            }
        }
    }

}

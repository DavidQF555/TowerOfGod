package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.RequirementTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerTechniqueData extends RequirementTechniqueData<Player> {

    private final Set<ShinsuTechnique> unlocked = new HashSet<>();

    public static PlayerTechniqueData get(Player player) {
        return player.getCapability(CAPABILITY).<PlayerTechniqueData>cast().orElseGet(PlayerTechniqueData::new);
    }

    public Set<ShinsuTechnique> getUnlocked() {
        return unlocked;
    }

    @Override
    public boolean isUnlocked(Player user, ShinsuTechnique technique) {
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        ListTag unlocked = new ListTag();
        getUnlocked().forEach(technique -> unlocked.add(StringTag.valueOf(technique.getId().toString())));
        tag.put("Unlocked", unlocked);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Unlocked", Tag.TAG_LIST)) {
            for (Tag tag : nbt.getList("Unlocked", Tag.TAG_STRING)) {
                unlock(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(tag.getAsString())));
            }
        }
    }

}

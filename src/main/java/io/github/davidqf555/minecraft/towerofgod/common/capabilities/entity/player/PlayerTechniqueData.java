package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.BaangsTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerTechniqueData extends BaangsTechniqueData<Player> {

    private final Set<ConfiguredShinsuTechniqueType<?, ?>> unlocked = new HashSet<>();

    public static PlayerTechniqueData get(Player player) {
        return player.getCapability(CAPABILITY).<PlayerTechniqueData>cast().orElseGet(PlayerTechniqueData::new);
    }

    @Override
    public void setBaangs(Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs) {
        baangs.keySet().removeIf(type -> !getUnlocked().contains(type));
        super.setBaangs(baangs);
    }

    public Set<ConfiguredShinsuTechniqueType<?, ?>> getUnlocked() {
        return unlocked;
    }

    public boolean unlock(ConfiguredShinsuTechniqueType<?, ?> technique) {
        return unlocked.add(technique);
    }

    public boolean lock(ConfiguredShinsuTechniqueType<?, ?> technique) {
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
                unlock(ConfiguredTechniqueTypeRegistry.getRegistry().getValue(new ResourceLocation(tag.getAsString())));
            }
        }
    }

}

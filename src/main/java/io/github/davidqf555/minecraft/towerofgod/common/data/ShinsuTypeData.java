package io.github.davidqf555.minecraft.towerofgod.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class ShinsuTypeData implements INBTSerializable<CompoundTag> {

    private int level, experience;

    public ShinsuTypeData() {
        level = 0;
        experience = 0;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(level, 0);
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Level", getLevel());
        nbt.putInt("Experience", getExperience());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Level", Tag.TAG_INT)) {
            setLevel(nbt.getInt("Level"));
        }
        if (nbt.contains("Experience", Tag.TAG_INT)) {
            setExperience(nbt.getInt("Experience"));
        }
    }
}

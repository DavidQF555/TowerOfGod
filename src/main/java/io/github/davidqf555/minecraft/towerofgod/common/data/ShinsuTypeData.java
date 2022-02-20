package io.github.davidqf555.minecraft.towerofgod.common.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class ShinsuTypeData implements INBTSerializable<CompoundNBT> {

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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Level", getLevel());
        nbt.putInt("Experience", getExperience());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            setLevel(nbt.getInt("Level"));
        }
        if (nbt.contains("Experience", Constants.NBT.TAG_INT)) {
            setExperience(nbt.getInt("Experience"));
        }
    }
}

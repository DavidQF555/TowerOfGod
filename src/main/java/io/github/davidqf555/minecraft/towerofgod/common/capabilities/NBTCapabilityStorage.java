package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class NBTCapabilityStorage<T extends INBT, M extends INBTSerializable<T>> implements Capability.IStorage<M> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<M> capability, M instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<M> capability, M instance, Direction side, INBT nbt) {
        instance.deserializeNBT((T) nbt);
    }

}

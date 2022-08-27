package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class SimpleCapabilityProvider<M> implements ICapabilitySerializable<INBT> {

    private final Capability<M> capability;
    private final LazyOptional<M> instance;

    public SimpleCapabilityProvider(Capability<M> capability) {
        this.capability = capability;
        this.instance = LazyOptional.of(capability::getDefaultInstance);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == capability ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return capability.getStorage().writeNBT(capability, instance.orElseThrow(NullPointerException::new), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        capability.getStorage().readNBT(capability, instance.orElseThrow(NullPointerException::new), null, nbt);
    }

}

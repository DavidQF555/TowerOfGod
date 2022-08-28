package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class SimpleCapabilityProvider<H extends Tag, M extends INBTSerializable<H>> implements ICapabilitySerializable<H> {

    private final Capability<M> capability;
    private final M instance;

    public SimpleCapabilityProvider(M instance, Capability<M> capability) {
        this.capability = capability;
        this.instance = instance;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == capability ? LazyOptional.of(() -> instance).cast() : LazyOptional.empty();
    }

    @Override
    public H serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(H nbt) {
        instance.deserializeNBT(nbt);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;

public interface IPlayerShinsuEquips extends INBTSerializable<ListNBT> {

    @Nonnull
    static IPlayerShinsuEquips get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(PlayerShinsuEquips::new);
    }

    ShinsuTechnique[] getEquipped();

    void setEquipped(ShinsuTechnique[] equipped);

    class PlayerShinsuEquips implements IPlayerShinsuEquips {

        private final ShinsuTechnique[] equipped;

        public PlayerShinsuEquips() {
            equipped = new ShinsuTechnique[4];
        }

        @Override
        public ShinsuTechnique[] getEquipped() {
            return equipped;
        }

        @Override
        public void setEquipped(ShinsuTechnique[] equipped) {
            int size = Math.min(equipped.length, this.equipped.length);
            System.arraycopy(equipped, 0, this.equipped, 0, size);
        }

        @Override
        public ListNBT serializeNBT() {
            ListNBT nbt = new ListNBT();
            for (ShinsuTechnique technique : equipped) {
                nbt.add(StringNBT.valueOf(technique == null ? "" : technique.name()));
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(ListNBT nbt) {
            setEquipped(nbt.stream().map(data -> {
                try {
                    return ShinsuTechnique.valueOf(data.getString());
                } catch (IllegalArgumentException exception) {
                    return null;
                }
            }).toArray(ShinsuTechnique[]::new));
        }

        public static class Factory implements Callable<IPlayerShinsuEquips> {
            @Override
            public IPlayerShinsuEquips call() {
                return new PlayerShinsuEquips();
            }
        }
    }

    class Provider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(IPlayerShinsuEquips.class)
        public static Capability<IPlayerShinsuEquips> capability = null;
        private final LazyOptional<IPlayerShinsuEquips> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

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

    class Storage implements Capability.IStorage<IPlayerShinsuEquips> {

        @Override
        public INBT writeNBT(Capability<IPlayerShinsuEquips> capability, IPlayerShinsuEquips instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IPlayerShinsuEquips> capability, IPlayerShinsuEquips instance, Direction side, INBT nbt) {
            instance.deserializeNBT((ListNBT) nbt);
        }
    }
}

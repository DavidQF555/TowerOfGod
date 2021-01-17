package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;

public interface IPlayerShinsuEquips {

    @Nonnull
    static IPlayerShinsuEquips get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(PlayerShinsuEquips::new);
    }

    ShinsuTechnique[] getEquipped();

    void setEquipped(ShinsuTechnique[] equipped);

    CompoundNBT serialize();

    void deserialize(CompoundNBT nbt);

    class PlayerShinsuEquips implements IPlayerShinsuEquips {

        private ShinsuTechnique[] equipped;

        public PlayerShinsuEquips() {
            equipped = new ShinsuTechnique[4];
        }

        @Override
        public ShinsuTechnique[] getEquipped() {
            return equipped;
        }

        @Override
        public void setEquipped(ShinsuTechnique[] equipped) {
            this.equipped = equipped;
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("Size", equipped.length);
            for (int i = 0; i < equipped.length; i++) {
                ShinsuTechnique technique = equipped[i];
                nbt.putString(i + 1 + "", technique == null ? "" : technique.getName());
            }
            return nbt;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {
            equipped = new ShinsuTechnique[nbt.getInt("Size")];
            for (int i = 0; i < equipped.length; i++) {
                equipped[i] = ShinsuTechnique.get(nbt.getString(i + 1 + ""));
            }
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
            return instance.serialize();
        }

        @Override
        public void readNBT(Capability<IPlayerShinsuEquips> capability, IPlayerShinsuEquips instance, Direction side, INBT nbt) {
            instance.deserialize((CompoundNBT) nbt);
        }
    }
}

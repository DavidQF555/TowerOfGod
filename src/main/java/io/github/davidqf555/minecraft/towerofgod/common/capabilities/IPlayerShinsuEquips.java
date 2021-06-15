package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;

public interface IPlayerShinsuEquips extends INBTSerializable<CompoundNBT> {

    @Nonnull
    static IPlayerShinsuEquips get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(PlayerShinsuEquips::new);
    }

    ShinsuTechnique[] getEquipped();

    void setEquipped(ShinsuTechnique[] equipped);

    String[] getSettings();

    void setSettings(String[] settings);

    class PlayerShinsuEquips implements IPlayerShinsuEquips {

        private final ShinsuTechnique[] equipped;
        private final String[] settings;

        public PlayerShinsuEquips() {
            equipped = new ShinsuTechnique[4];
            settings = new String[4];
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
        public String[] getSettings() {
            return settings;
        }

        @Override
        public void setSettings(String[] settings) {
            int size = Math.min(settings.length, this.settings.length);
            System.arraycopy(settings, 0, this.settings, 0, size);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT equipped = new ListNBT();
            for (ShinsuTechnique technique : this.equipped) {
                equipped.add(StringNBT.valueOf(technique == null ? "" : technique.name()));
            }
            nbt.put("Equipped", equipped);
            ListNBT settings = new ListNBT();
            for (String value : this.settings) {
                settings.add(StringNBT.valueOf(value == null ? "" : value));
            }
            nbt.put("Settings", settings);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            if (nbt.contains("Equipped", Constants.NBT.TAG_LIST)) {
                setEquipped(nbt.getList("Equipped", Constants.NBT.TAG_STRING).stream().map(data -> {
                    try {
                        return ShinsuTechnique.valueOf(data.getString());
                    } catch (IllegalArgumentException exception) {
                        return null;
                    }
                }).toArray(ShinsuTechnique[]::new));
            }
            if (nbt.contains("Settings", Constants.NBT.TAG_LIST)) {
                String[] settings = nbt.getList("Settings", Constants.NBT.TAG_STRING).stream().map(INBT::getString).toArray(String[]::new);
                ShinsuTechnique[] equipped = getEquipped();
                for (int i = 0; i < settings.length; i++) {
                    if (i >= equipped.length || equipped[i] == null) {
                        settings[i] = null;
                    }
                }
                setSettings(settings);
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
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IPlayerShinsuEquips> capability, IPlayerShinsuEquips instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}

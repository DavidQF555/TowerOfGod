package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.TechniqueSettings;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public interface IPlayerShinsuEquips extends INBTSerializable<CompoundNBT> {

    @Nonnull
    static IPlayerShinsuEquips get(Entity user) {
        return user.getCapability(Provider.capability).orElseGet(PlayerShinsuEquips::new);
    }

    List<Pair<ShinsuTechnique, String>> getEquipped();

    void setEquipped(List<Pair<ShinsuTechnique, String>> equipped);

    class PlayerShinsuEquips implements IPlayerShinsuEquips {

        private final List<Pair<ShinsuTechnique, String>> equipped;

        public PlayerShinsuEquips() {
            equipped = new ArrayList<>();
        }

        @Override
        public List<Pair<ShinsuTechnique, String>> getEquipped() {
            return equipped;
        }

        @Override
        public void setEquipped(List<Pair<ShinsuTechnique, String>> equipped) {
            this.equipped.clear();
            this.equipped.addAll(equipped);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT techniques = new ListNBT();
            ListNBT settings = new ListNBT();
            for (Pair<ShinsuTechnique, String> pair : this.equipped) {
                techniques.add(StringNBT.valueOf(pair.getFirst().name()));
                settings.add(StringNBT.valueOf(pair.getSecond()));
            }
            nbt.put("Techniques", techniques);
            nbt.put("Settings", settings);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            if (nbt.contains("Techniques", Constants.NBT.TAG_LIST)) {
                ListNBT techniques = nbt.getList("Techniques", Constants.NBT.TAG_STRING);
                ListNBT settings;
                if (nbt.contains("Settings", Constants.NBT.TAG_LIST)) {
                    settings = nbt.getList("Settings", Constants.NBT.TAG_STRING);
                } else {
                    settings = new ListNBT();
                }
                List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
                for (int i = 0; i < techniques.size(); i++) {
                    ShinsuTechnique technique = ShinsuTechnique.valueOf(techniques.getString(i));
                    String s = i < settings.size() ? settings.getString(i) : "";
                    TechniqueSettings set = technique.getSettings();
                    equipped.add(Pair.of(technique, set.getOptions().contains(s) ? s : set.getDefault()));
                }
                setEquipped(equipped);
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

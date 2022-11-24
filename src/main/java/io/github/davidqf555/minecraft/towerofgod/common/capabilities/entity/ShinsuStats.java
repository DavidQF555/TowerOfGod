package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class ShinsuStats implements INBTSerializable<CompoundNBT> {

    @CapabilityInject(ShinsuStats.class)
    public static Capability<ShinsuStats> capability = null;
    private double resistance = 1;
    private double tension = 1;
    private int shinsu;

    public static ShinsuStats get(Entity entity) {
        return entity.getCapability(capability).orElseGet(ShinsuStats::new);
    }

    public static int getShinsu(Entity entity) {
        return get(entity).getMaxShinsu() - ShinsuTechniqueData.get(entity).getShinsuUsage();
    }

    public static double getNetResistance(Entity user, Entity target) {
        ShinsuStats targetStats = get(target);
        ShinsuStats userStats = get(user);
        return targetStats.getResistance() / userStats.getTension();
    }

    public int getMaxShinsu() {
        return shinsu;
    }

    public void setMaxShinsu(int shinsu) {
        this.shinsu = shinsu;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getTension() {
        return tension;
    }

    public void setTension(double tension) {
        this.tension = tension;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putDouble("Resistance", getResistance());
        tag.putDouble("Tension", getTension());
        tag.putInt("Shinsu", getMaxShinsu());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Resistance", Constants.NBT.TAG_DOUBLE)) {
            setResistance(nbt.getDouble("Resistance"));
        }
        if (nbt.contains("Tension", Constants.NBT.TAG_DOUBLE)) {
            setTension(nbt.getDouble("Tension"));
        }
        if (nbt.contains("Shinsu", Constants.NBT.TAG_INT)) {
            setMaxShinsu(nbt.getInt("Shinsu"));
        }
    }

}

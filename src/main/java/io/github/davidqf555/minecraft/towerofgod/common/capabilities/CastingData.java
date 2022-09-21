package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ByteNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class CastingData implements INBTSerializable<ByteNBT> {

    @CapabilityInject(CastingData.class)
    public static Capability<CastingData> capability = null;
    private boolean casting;

    public static CastingData get(PlayerEntity player) {
        return player.getCapability(capability).orElseGet(CastingData::new);
    }

    public boolean isCasting() {
        return casting;
    }

    public void setCasting(boolean casting) {
        this.casting = casting;
    }


    @Override
    public ByteNBT serializeNBT() {
        return ByteNBT.valueOf(isCasting());
    }

    @Override
    public void deserializeNBT(ByteNBT nbt) {
        setCasting(nbt.getAsByte() != 0);
    }

}

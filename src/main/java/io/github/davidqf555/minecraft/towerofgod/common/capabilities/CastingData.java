package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import net.minecraft.nbt.ByteTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public class CastingData implements INBTSerializable<ByteTag> {

    public static final Capability<CastingData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private boolean casting;

    public static CastingData get(Player player) {
        return player.getCapability(CAPABILITY).orElseGet(CastingData::new);
    }

    public boolean isCasting() {
        return casting;
    }

    public void setCasting(boolean casting) {
        this.casting = casting;
    }


    @Override
    public ByteTag serializeNBT() {
        return ByteTag.valueOf(isCasting());
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        setCasting(nbt.getAsByte() != 0);
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;

public final class NoData {

    public static final NoData INSTANCE = new NoData();
    public static final Codec<NoData> CODEC = Codec.unit(INSTANCE);

    private NoData() {
    }

}

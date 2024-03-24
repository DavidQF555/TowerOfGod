package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MobUseConditionType extends ForgeRegistryEntry<MobUseConditionType> {

    private final Codec<? extends MobUseCondition> codec;

    public MobUseConditionType(Codec<? extends MobUseCondition> codec) {
        this.codec = codec;
    }

    public Codec<? extends MobUseCondition> getCodec() {
        return codec;
    }

}

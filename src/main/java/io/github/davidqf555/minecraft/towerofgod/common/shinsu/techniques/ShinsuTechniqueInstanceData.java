package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;

import java.util.UUID;

public class ShinsuTechniqueInstanceData {

    public static final Codec<ShinsuTechniqueInstanceData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id)
    ).apply(inst, ShinsuTechniqueInstanceData::new));
    public final UUID id;

    public ShinsuTechniqueInstanceData(UUID id) {
        this.id = id;
    }

}

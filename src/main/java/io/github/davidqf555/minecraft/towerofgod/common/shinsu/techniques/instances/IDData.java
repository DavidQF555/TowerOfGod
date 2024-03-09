package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class IDData {

    public static final Codec<IDData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Util.UUID_CODEC.fieldOf("ID").forGetter(data -> data.id)
    ).apply(inst, IDData::new));
    public final UUID id;

    protected IDData(UUID id) {
        this.id = id;
    }

    @Nullable
    public static ShinsuTechniqueInstance<?, ? extends IDData> getTechnique(LivingEntity user, UUID id) {
        return ShinsuTechniqueData.get(user).getTechniques().stream()
                .filter(inst -> inst.getData() instanceof IDData)
                .filter(inst -> id.equals(((IDData) inst.getData()).id))
                .map(inst -> (ShinsuTechniqueInstance<?, ? extends IDData>) inst)
                .findAny().orElse(null);
    }

}

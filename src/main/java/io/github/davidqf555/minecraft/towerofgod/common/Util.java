package io.github.davidqf555.minecraft.towerofgod.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.SerializableUUID;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.stream.IntStream;

public final class Util {

    public static final Codec<UUID> UUID_CODEC = Codec.INT_STREAM.xmap(
            stream -> SerializableUUID.uuidFromIntArray(stream.toArray()),
            id -> IntStream.of(SerializableUUID.uuidToIntArray(id))
    );
    public static final Codec<Vec3> VEC3_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
    ).apply(inst, Vec3::new));
    public static final Codec<TranslatableComponent> TRANSLATABLE_CODEC = Codec.STRING.xmap(TranslatableComponent::new, TranslatableComponent::getKey);

    private Util() {
    }

}

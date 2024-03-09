package io.github.davidqf555.minecraft.towerofgod.common;

import com.mojang.serialization.Codec;
import net.minecraft.core.SerializableUUID;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.UUID;
import java.util.stream.IntStream;

public final class Util {

    public static final Codec<UUID> UUID_CODEC = Codec.INT_STREAM.xmap(
            stream -> SerializableUUID.uuidFromIntArray(stream.toArray()),
            id -> IntStream.of(SerializableUUID.uuidToIntArray(id))
    );
    public static final Codec<TranslatableComponent> TRANSLATABLE_CODEC = Codec.STRING.xmap(TranslatableComponent::new, TranslatableComponent::getKey);

    private Util() {
    }

}

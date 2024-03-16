package io.github.davidqf555.minecraft.towerofgod.common;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.UUID;
import java.util.stream.IntStream;

public final class Util {

    public static final Codec<UUID> UUID_CODEC = Codec.INT_STREAM.xmap(
            stream -> UUIDUtil.uuidFromIntArray(stream.toArray()),
            id -> IntStream.of(UUIDUtil.uuidToIntArray(id))
    );
    public static final Codec<MutableComponent> TRANSLATABLE_CODEC = Codec.STRING.xmap(Component::translatable, comp -> ((TranslatableContents) comp.getContents()).getKey());

    private Util() {
    }

}

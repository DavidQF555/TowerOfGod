package io.github.davidqf555.minecraft.towerofgod.common.data;

public interface IRenderData {

    Type getType();

    enum Type {

        TEXTURE(),
        ITEM()

    }
}

package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import net.minecraft.client.gui.IRenderable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public interface IClientRenderData extends IRenderable {

    Map<IRenderData.Type, Function<IRenderData, IClientRenderData>> FACTORIES = new EnumMap<>(IRenderData.Type.class);

    static void registerType(IRenderData.Type type, Function<IRenderData, IClientRenderData> factory) {
        FACTORIES.put(type, factory);
    }

    static IClientRenderData convert(IRenderData data) {
        return FACTORIES.get(data.getType()).apply(data);
    }

    void render(MatrixStack matrixStack, float x, float y, float blitOffset, int width, int height, int color);

    @Override
    default void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        render(matrixStack, 0, 0, 0, 0, 0, 0xFFFFFFFF);
    }
}

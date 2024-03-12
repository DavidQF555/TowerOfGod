package io.github.davidqf555.minecraft.towerofgod.common.data;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import net.minecraft.resources.ResourceLocation;

public class FullTextureRenderData implements IRenderData {

    private final ResourceLocation texture;

    public FullTextureRenderData(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void render(RenderContext context) {
        ClientReference.renderFullTexture(texture, context);
    }

}

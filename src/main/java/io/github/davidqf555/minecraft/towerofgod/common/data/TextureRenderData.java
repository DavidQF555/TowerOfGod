package io.github.davidqf555.minecraft.towerofgod.common.data;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import net.minecraft.util.ResourceLocation;

public class TextureRenderData implements IRenderData {

    private final ResourceLocation texture;
    private final int textureWidth, textureHeight, startX;
    private int blitHeight, startY, blitWidth;

    public TextureRenderData(ResourceLocation texture, int textureWidth, int textureHeight, int startX, int startY, int blitWidth, int blitHeight) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.startX = startX;
        this.startY = startY;
        this.blitWidth = blitWidth;
        this.blitHeight = blitHeight;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getBlitWidth() {
        return blitWidth;
    }

    public void setBlitWidth(int blitWidth) {
        this.blitWidth = blitWidth;
    }

    public int getBlitHeight() {
        return blitHeight;
    }

    public void setBlitHeight(int blitHeight) {
        this.blitHeight = blitHeight;
    }

    @Override
    public void render(RenderContext context) {
        ClientReference.renderTextureData(this, context);
    }
}

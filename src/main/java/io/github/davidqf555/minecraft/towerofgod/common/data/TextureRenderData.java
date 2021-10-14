package io.github.davidqf555.minecraft.towerofgod.common.data;

import net.minecraft.util.ResourceLocation;

public class TextureRenderData implements IRenderData {

    protected ResourceLocation texture;
    protected int textureWidth;
    protected int textureHeight;
    protected int startX;
    protected int startY;
    protected int blitWidth;
    protected int blitHeight;

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

    public int getBlitWidth() {
        return blitWidth;
    }

    public int getBlitHeight() {
        return blitHeight;
    }

    @Override
    public Type getType() {
        return Type.TEXTURE;
    }
}

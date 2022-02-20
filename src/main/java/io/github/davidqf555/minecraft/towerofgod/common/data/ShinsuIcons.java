package io.github.davidqf555.minecraft.towerofgod.common.data;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.util.ResourceLocation;

public final class ShinsuIcons {

    private static final ResourceLocation ICONS = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu_icons.png");

    public static final TextureRenderData SWIRL = new TextureRenderData(ICONS, 64, 64, 0, 0, 16, 16);
    public static final TextureRenderData RESISTANCE = new TextureRenderData(ICONS, 64, 64, 16, 0, 16, 16);
    public static final TextureRenderData BAANGS = new TextureRenderData(ICONS, 64, 64, 32, 0, 16, 16);
    public static final TextureRenderData TENSION = new TextureRenderData(ICONS, 64, 64, 48, 0, 16, 16);
    public static final TextureRenderData REVERSE = new TextureRenderData(ICONS, 64, 64, 0, 16, 16, 16);
    public static final TextureRenderData SHINSU = new TextureRenderData(ICONS, 64, 64, 16, 16, 16, 14);
    public static final TextureRenderData PICKAXE = new TextureRenderData(ICONS, 64, 64, 32, 16, 16, 16);
    public static final TextureRenderData MOVE = new TextureRenderData(ICONS, 64, 64, 48, 16, 16, 16);
    public static final TextureRenderData LIGHTHOUSE_FLOW_CONTROL = new TextureRenderData(ICONS, 64, 64, 0, 32, 16, 16);
    public static final TextureRenderData EYE = new TextureRenderData(ICONS, 64, 64, 16, 32, 16, 16);
    public static final TextureRenderData FOLLOW = new TextureRenderData(ICONS, 64, 64, 32, 32, 16, 16);
    public static final TextureRenderData LIGHTNING = new TextureRenderData(ICONS, 64, 64, 48, 32, 16, 16);
    public static final TextureRenderData FLASH = new TextureRenderData(ICONS, 64, 64, 0, 48, 16, 16);

    private ShinsuIcons() {
    }
}

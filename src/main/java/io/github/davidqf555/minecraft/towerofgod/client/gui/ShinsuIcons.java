package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderInfo;
import net.minecraft.util.ResourceLocation;

public class ShinsuIcons {

    private static final ResourceLocation ICONS = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_icons.png");

    public static final RenderInfo SWIRL = new RenderInfo(ICONS, 64, 64, 0, 0, 16, 16);
    public static final RenderInfo RESISTANCE = new RenderInfo(ICONS, 64, 64, 16, 0, 16, 16);
    public static final RenderInfo BAANGS = new RenderInfo(ICONS, 64, 64, 32, 0, 16, 16);
    public static final RenderInfo TENSION = new RenderInfo(ICONS, 64, 64, 48, 0, 16, 16);
    public static final RenderInfo REVERSE = new RenderInfo(ICONS, 64, 64, 0, 16, 16, 16);
    public static final RenderInfo SHINSU = new RenderInfo(ICONS, 64, 64, 16, 16, 16, 14);
    public static final RenderInfo PICKAXE = new RenderInfo(ICONS, 64, 64, 32, 16, 16, 16);
    public static final RenderInfo MOVE = new RenderInfo(ICONS, 64, 64, 48, 16, 16, 16);
    public static final RenderInfo LIGHTHOUSE_FLOW_CONTROL = new RenderInfo(ICONS, 64, 64, 0, 32, 16, 16);
    public static final RenderInfo EYE = new RenderInfo(ICONS, 64, 64, 16, 32, 16, 16);
    public static final RenderInfo FOLLOW = new RenderInfo(ICONS, 64, 64, 32, 32, 16, 16);

}

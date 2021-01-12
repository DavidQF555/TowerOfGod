package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import net.minecraft.util.ResourceLocation;

public class ShinsuIcons {

    private static final ResourceLocation ICONS = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_icons.png");

    public static final RenderInfo SWIRL = new RenderInfo(ICONS, 64, 64, 0, 0, 16, 16);
    public static final RenderInfo RESISTANCE = new RenderInfo(ICONS, 64, 64, 16, 0, 16, 16);
    public static final RenderInfo BAANGS = new RenderInfo(ICONS, 64, 64, 32, 0, 16, 16);
    public static final RenderInfo TENSION = new RenderInfo(ICONS, 64, 64, 48, 0, 16, 16);
    public static final RenderInfo REVERSE = new RenderInfo(ICONS, 64, 64, 0, 16, 16, 16);
    public static final RenderInfo SHINSU = new RenderInfo(ICONS, 64, 64, 16, 16, 16, 14);
}

package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.tools.HookItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;

public enum Family {

    ARIE("arie", new ShinsuQuality[]{}, new ShinsuTechnique[]{}, new Class[]{SwordItem.class}, 1, 1),
    EURASIA("eurasia", new ShinsuQuality[]{ShinsuQuality.WIND}, new ShinsuTechnique[] {}, new Class[]{}, 1, 1.5),
    HA("ha", new ShinsuQuality[]{}, new ShinsuTechnique[]{}, new Class[]{HookItem.class}, 1.5, 1),
    KHUN("khun", new ShinsuQuality[]{ShinsuQuality.ICE, ShinsuQuality.LIGHTNING}, new ShinsuTechnique[]{}, new Class[]{}, 1, 1),
    YEON("yeon", new ShinsuQuality[]{ShinsuQuality.FIRE}, new ShinsuTechnique[] {}, new Class[]{}, 1, 1.2);

    private final ResourceLocation texture;
    private final ShinsuQuality[] qualities;
    private final ShinsuTechnique[] techniques;
    private final Class<? extends Item>[] weapons;
    private final double resistance;
    private final double shinsu;

    Family(String texture, ShinsuQuality[] qualities, ShinsuTechnique[] techniques, Class<? extends Item>[] weapons, double resistance, double shinsu) {
        this.texture = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/regular/" + texture + "_entity.png");
        this.qualities = qualities;
        this.techniques = techniques;
        this.weapons = weapons;
        this.resistance = resistance;
        this.shinsu = shinsu;
    }

    public static Family get(String name) {
        for (Family family : values()) {
            if (family.name().equals(name)) {
                return family;
            }
        }
        return null;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ShinsuQuality[] getQualities() {
        return qualities;
    }

    public ShinsuTechnique[] getPreferredTechniques() {
        return techniques;
    }

    public Class<? extends Item>[] getWeapons() {
        return weapons;
    }

    public double getResistance() {
        return resistance;
    }

    public double getShinsu() {
        return shinsu;
    }
}

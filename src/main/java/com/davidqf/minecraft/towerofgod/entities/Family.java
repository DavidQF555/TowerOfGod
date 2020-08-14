package com.davidqf.minecraft.towerofgod.entities;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.entities.shinsu.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.tools.HookItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public enum Family {

    ARIE("arie", new ShinsuQuality[]{}, Arrays.asList(), new Class[]{SwordItem.class}, 1, 1),
    EURASIA("eurasia", new ShinsuQuality[]{ShinsuQuality.WIND}, Arrays.asList(), new Class[]{}, 1, 1.5),
    HA("ha", new ShinsuQuality[]{}, Arrays.asList(), new Class[]{HookItem.class}, 1.5, 1),
    KHUN("khun", new ShinsuQuality[]{ShinsuQuality.ICE, ShinsuQuality.LIGHTNING}, Arrays.asList(), new Class[]{}, 1, 1),
    YEON("yeon", new ShinsuQuality[]{ShinsuQuality.FIRE}, Arrays.asList(), new Class[]{}, 1, 1.2);

    private final ResourceLocation texture;
    private final ShinsuQuality[] qualities;
    private final List<ShinsuTechniques> techniques;
    private final Class<? extends Item>[] weapons;
    private final double resistance;
    private final double shinsu;

    Family(String texture, ShinsuQuality[] qualities, List<ShinsuTechniques> techniques, Class<? extends Item>[] weapons, double resistance, double shinsu) {
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

    public List<ShinsuTechniques> getPreferredTechniques() {
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

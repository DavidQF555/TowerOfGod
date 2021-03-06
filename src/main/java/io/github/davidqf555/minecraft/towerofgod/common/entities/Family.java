package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;

public enum Family {

    ARIE("arie", new ShinsuQuality[]{}, new ShinsuShape[]{ShinsuShape.SWORD}, new ShinsuTechnique[]{}, new Class[]{SwordItem.class}, 1, 1, 1, 1),
    EURASIA("eurasia", new ShinsuQuality[]{ShinsuQuality.WIND}, new ShinsuShape[]{}, new ShinsuTechnique[]{}, new Class[]{}, 1, 1.5, 2, 2),
    HA("ha", new ShinsuQuality[]{}, new ShinsuShape[]{}, new ShinsuTechnique[]{}, new Class[]{HookItem.class}, 2, 1, 1, 1),
    KHUN("khun", new ShinsuQuality[]{ShinsuQuality.ICE, ShinsuQuality.LIGHTNING}, new ShinsuShape[]{}, new ShinsuTechnique[]{}, new Class[]{}, 1, 1, 1, 1),
    YEON("yeon", new ShinsuQuality[]{ShinsuQuality.FIRE}, new ShinsuShape[]{}, new ShinsuTechnique[]{}, new Class[]{}, 1, 1.2, 1.5, 1);

    private final ResourceLocation texture;
    private final ShinsuQuality[] qualities;
    private final ShinsuShape[] shapes;
    private final ShinsuTechnique[] techniques;
    private final Class<? extends Item>[] weapons;
    private final double resistance;
    private final double tension;
    private final double shinsu;
    private final double baangs;

    Family(String texture, ShinsuQuality[] qualities, ShinsuShape[] shapes, ShinsuTechnique[] techniques, Class<? extends Item>[] weapons, double resistance, double tension, double shinsu, double baangs) {
        this.texture = new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/regular/" + texture + "_entity.png");
        this.qualities = qualities;
        this.shapes = shapes;
        this.techniques = techniques;
        this.weapons = weapons;
        this.resistance = resistance;
        this.tension = tension;
        this.shinsu = shinsu;
        this.baangs = baangs;
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

    public ShinsuShape[] getShapes() {
        return shapes;
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

    public double getTension() {
        return tension;
    }

    public double getShinsu() {
        return shinsu;
    }

    public double getBaangs() {
        return baangs;
    }
}

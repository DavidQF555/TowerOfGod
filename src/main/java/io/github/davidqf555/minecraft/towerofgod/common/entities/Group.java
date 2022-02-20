package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;

import java.util.function.Predicate;

public enum Group {

    NONE(new ResourceLocation("textures/entity/steve.png"), 0xFFFFFFFF, new ShinsuQuality[0], new ShinsuShape[0], new ShinsuTechniqueType[0], item -> false, 1, 1, 1, 1),
    ARIE("arie", 0xFFFFFFFF, new ShinsuQuality[0], new ShinsuShape[]{ShinsuShape.SWORD}, new ShinsuTechniqueType[0], item -> item instanceof SwordItem, 1, 1, 1, 1),
    EURASIA("eurasia", 0xFF00FF7F, new ShinsuQuality[]{ShinsuQuality.WIND}, new ShinsuShape[0], new ShinsuTechniqueType[0], item -> false, 1, 1.5, 2, 2),
    HA("ha", 0xFFDC143C, new ShinsuQuality[0], new ShinsuShape[0], new ShinsuTechniqueType[0], item -> item instanceof HookItem, 2, 1, 1, 1),
    KHUN("khun", 0xFF6495ED, new ShinsuQuality[]{ShinsuQuality.ICE, ShinsuQuality.LIGHTNING}, new ShinsuShape[0], new ShinsuTechniqueType[0], item -> false, 1, 1, 1, 1),
    YEON("yeon", 0xFFFF1493, new ShinsuQuality[]{ShinsuQuality.FIRE}, new ShinsuShape[0], new ShinsuTechniqueType[0], item -> false, 1, 1.2, 1.5, 1);

    private final ResourceLocation texture;
    private final int color;
    private final TextFormatting format;
    private final BossInfo.Color bossColor;
    private final ShinsuQuality[] qualities;
    private final ShinsuShape[] shapes;
    private final ShinsuTechniqueType[] types;
    private final Predicate<Item> weapons;
    private final double resistance;
    private final double tension;
    private final double shinsu;
    private final double baangs;

    Group(ResourceLocation texture, int color, ShinsuQuality[] qualities, ShinsuShape[] shapes, ShinsuTechniqueType[] types, Predicate<Item> weapons, double resistance, double tension, double shinsu, double baangs) {
        this.texture = texture;
        this.color = color;
        bossColor = getBossInfoColor(color);
        format = getTextFormattingColor(color);
        this.qualities = qualities;
        this.shapes = shapes;
        this.types = types;
        this.weapons = weapons;
        this.resistance = resistance;
        this.tension = tension;
        this.shinsu = shinsu;
        this.baangs = baangs;
    }

    Group(String texture, int color, ShinsuQuality[] qualities, ShinsuShape[] shapes, ShinsuTechniqueType[] types, Predicate<Item> weapons, double resistance, double tension, double shinsu, double baangs) {
        this(new ResourceLocation(TowerOfGod.MOD_ID, "textures/entity/group/" + texture + ".png"), color, qualities, shapes, types, weapons, resistance, tension, shinsu, baangs);
    }

    private static BossInfo.Color getBossInfoColor(int color) {
        int red = ColorHelper.PackedColor.getRed(color);
        int green = ColorHelper.PackedColor.getGreen(color);
        int blue = ColorHelper.PackedColor.getBlue(color);
        BossInfo.Color closest = BossInfo.Color.WHITE;
        int min = 766;
        for (BossInfo.Color info : BossInfo.Color.values()) {
            TextFormatting format = info.getFormatting();
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - ColorHelper.PackedColor.getRed(hex)) + Math.abs(blue - ColorHelper.PackedColor.getBlue(hex)) + Math.abs(green - ColorHelper.PackedColor.getGreen(hex));
                if (dif < min) {
                    min = dif;
                    closest = info;
                }
            }
        }
        return closest;
    }

    private static TextFormatting getTextFormattingColor(int color) {
        int red = ColorHelper.PackedColor.getRed(color);
        int green = ColorHelper.PackedColor.getGreen(color);
        int blue = ColorHelper.PackedColor.getBlue(color);
        TextFormatting closest = TextFormatting.WHITE;
        int min = 766;
        for (TextFormatting format : TextFormatting.values()) {
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - ColorHelper.PackedColor.getRed(hex)) + Math.abs(blue - ColorHelper.PackedColor.getBlue(hex)) + Math.abs(green - ColorHelper.PackedColor.getGreen(hex));
                if (dif < min) {
                    min = dif;
                    closest = format;
                }
            }
        }
        return closest;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getColor() {
        return color;
    }

    public BossInfo.Color getBossInfoColor() {
        return bossColor;
    }

    public TextFormatting getTextFormattingColor() {
        return format;
    }

    public ShinsuQuality[] getQualities() {
        return qualities;
    }

    public ShinsuShape[] getShapes() {
        return shapes;
    }

    public ShinsuTechniqueType[] getPreferredTechniqueTypes() {
        return types;
    }

    public boolean isPreferredWeapon(Item item) {
        return weapons.test(item);
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

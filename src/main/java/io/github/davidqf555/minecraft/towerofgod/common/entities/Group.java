package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import net.minecraft.item.Item;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Group extends ForgeRegistryEntry<Group> {

    private final int color;
    private final TextFormatting format;
    private final BossInfo.Color bossColor;
    private final Supplier<ShinsuAttribute[]> attributes;
    private final Supplier<ShinsuShape[]> shapes;
    private final Predicate<Item> weapons;
    private final double resistance;
    private final double tension;
    private final double shinsu;

    public Group(int color, Supplier<ShinsuAttribute[]> attributes, Supplier<ShinsuShape[]> shapes, Predicate<Item> weapons, double resistance, double tension, double shinsu) {
        this.color = color;
        bossColor = getBossInfoColor(color);
        format = getTextFormattingColor(color);
        this.attributes = attributes;
        this.shapes = shapes;
        this.weapons = weapons;
        this.resistance = resistance;
        this.tension = tension;
        this.shinsu = shinsu;
    }

    private static BossInfo.Color getBossInfoColor(int color) {
        int red = ColorHelper.PackedColor.red(color);
        int green = ColorHelper.PackedColor.green(color);
        int blue = ColorHelper.PackedColor.blue(color);
        BossInfo.Color closest = BossInfo.Color.WHITE;
        int min = Integer.MAX_VALUE;
        for (BossInfo.Color info : BossInfo.Color.values()) {
            TextFormatting format = info.getFormatting();
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - ColorHelper.PackedColor.red(hex)) + Math.abs(blue - ColorHelper.PackedColor.blue(hex)) + Math.abs(green - ColorHelper.PackedColor.green(hex));
                if (dif < min) {
                    min = dif;
                    closest = info;
                }
            }
        }
        return closest;
    }

    private static TextFormatting getTextFormattingColor(int color) {
        int red = ColorHelper.PackedColor.red(color);
        int green = ColorHelper.PackedColor.green(color);
        int blue = ColorHelper.PackedColor.blue(color);
        TextFormatting closest = TextFormatting.WHITE;
        int min = Integer.MAX_VALUE;
        for (TextFormatting format : TextFormatting.values()) {
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - ColorHelper.PackedColor.red(hex)) + Math.abs(blue - ColorHelper.PackedColor.blue(hex)) + Math.abs(green - ColorHelper.PackedColor.green(hex));
                if (dif < min) {
                    min = dif;
                    closest = format;
                }
            }
        }
        return closest;
    }

    public ResourceLocation getTexture() {
        ResourceLocation name = getRegistryName();
        return new ResourceLocation(name.getNamespace(), "textures/entity/group/" + name.getPath() + ".png");
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

    public ShinsuAttribute[] getAttributes() {
        return attributes.get();
    }

    public ShinsuShape[] getShapes() {
        return shapes.get();
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

    public TranslationTextComponent getName() {
        return new TranslationTextComponent(Util.makeDescriptionId("group", getRegistryName()));
    }

}

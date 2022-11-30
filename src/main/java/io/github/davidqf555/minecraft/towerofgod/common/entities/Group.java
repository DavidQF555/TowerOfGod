package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Group extends ForgeRegistryEntry<Group> {

    private final int color;
    private final ChatFormatting format;
    private final BossEvent.BossBarColor bossColor;
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

    private static BossEvent.BossBarColor getBossInfoColor(int color) {
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        BossEvent.BossBarColor closest = BossEvent.BossBarColor.WHITE;
        int min = Integer.MAX_VALUE;
        for (BossEvent.BossBarColor info : BossEvent.BossBarColor.values()) {
            ChatFormatting format = info.getFormatting();
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - FastColor.ARGB32.red(hex)) + Math.abs(blue - FastColor.ARGB32.blue(hex)) + Math.abs(green - FastColor.ARGB32.green(hex));
                if (dif < min) {
                    min = dif;
                    closest = info;
                }
            }
        }
        return closest;
    }

    private static ChatFormatting getTextFormattingColor(int color) {
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        ChatFormatting closest = ChatFormatting.WHITE;
        int min = Integer.MAX_VALUE;
        for (ChatFormatting format : ChatFormatting.values()) {
            if (format.isColor()) {
                int hex = format.getColor();
                int dif = Math.abs(red - FastColor.ARGB32.red(hex)) + Math.abs(blue - FastColor.ARGB32.blue(hex)) + Math.abs(green - FastColor.ARGB32.green(hex));
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

    public BossEvent.BossBarColor getBossInfoColor() {
        return bossColor;
    }

    public ChatFormatting getTextFormattingColor() {
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

    public TranslatableComponent getName() {
        return new TranslatableComponent(Util.makeDescriptionId("group", getRegistryName()));
    }

}

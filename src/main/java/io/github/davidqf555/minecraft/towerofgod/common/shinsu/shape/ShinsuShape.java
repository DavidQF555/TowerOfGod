package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShinsuShape extends ForgeRegistryEntry<ShinsuShape> {

    private final NonNullSupplier<ItemStack> item;

    public ShinsuShape(NonNullSupplier<ItemStack> item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item.get();
    }

    public ITextComponent getName() {
        return new TranslationTextComponent(Util.makeDescriptionId("shape", getRegistryName()));
    }

}

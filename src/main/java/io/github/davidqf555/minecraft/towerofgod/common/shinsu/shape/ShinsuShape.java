package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
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

    public Component getName() {
        return new TranslatableComponent(Util.makeDescriptionId("shape", getRegistryName()));
    }

}

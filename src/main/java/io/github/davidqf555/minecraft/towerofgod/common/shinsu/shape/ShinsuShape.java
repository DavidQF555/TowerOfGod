package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape;

import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullSupplier;

public class ShinsuShape {

    private final NonNullSupplier<ItemStack> item;
    private ResourceLocation id;

    public ShinsuShape(NonNullSupplier<ItemStack> item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item.get();
    }

    public Component getName() {
        return Component.translatable(Util.makeDescriptionId("shape", getId()));
    }

    public ResourceLocation getId() {
        if (id == null) {
            id = ShinsuShapeRegistry.getRegistry().getKey(this);
        }
        return id;
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.data;

import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ItemStackRenderData implements IRenderData, Supplier<ItemStack> {

    private final Supplier<ItemStack> item;

    public ItemStackRenderData(Supplier<ItemStack> item) {
        this.item = item;
    }

    @Override
    public ItemStack get() {
        return item.get();
    }

    @Override
    public Type getType() {
        return Type.ITEM;
    }
}

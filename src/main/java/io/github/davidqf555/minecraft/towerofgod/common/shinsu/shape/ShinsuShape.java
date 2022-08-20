package io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.suitability.SuitabilityCalculator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShinsuShape extends ForgeRegistryEntry<ShinsuShape> {

    private final SuitabilityCalculator suitability;
    private final NonNullSupplier<ItemStack> item;

    public ShinsuShape(NonNullSupplier<ItemStack> item, SuitabilityCalculator suitability) {
        this.item = item;
        this.suitability = suitability;
    }

    public ItemStack getItem() {
        return item.get();
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.calculate(player);
    }

    public ITextComponent getName() {
        return new TranslationTextComponent(Util.makeTranslationKey("shape", getRegistryName()));
    }

}

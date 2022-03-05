package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Function;

public enum ShinsuShape {

    NONE(() -> ItemStack.EMPTY, player -> -1.0),
    SHOVEL(() -> RegistryHandler.SHINSU_SHOVEL.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.SHOVEL) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }),
    PICKAXE(() -> RegistryHandler.SHINSU_PICKAXE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.PICKAXE) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }),
    AXE(() -> RegistryHandler.SHINSU_AXE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.AXE) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }),
    SWORD(() -> RegistryHandler.SHINSU_SWORD.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.CUSTOM.get(Stats.DAMAGE_DEALT)) * 0.1),
    HOE(() -> RegistryHandler.SHINSU_HOE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Block crop : BlockTags.CROPS.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED.get(crop)) * 4;
        }
        return amount;
    }),
    BOW(() -> RegistryHandler.SHINSU_BOW.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.ITEM_USED.get(Items.BOW)) * 2.0);

    private final NonNullSupplier<ItemStack> item;
    private final Function<ServerPlayerEntity, Double> suitability;
    private final TranslationTextComponent name;

    ShinsuShape(NonNullSupplier<ItemStack> item, Function<ServerPlayerEntity, Double> suitability) {
        this.item = item;
        this.suitability = suitability;
        name = new TranslationTextComponent("shape.towerofgod." + name().toLowerCase());
    }

    public ItemStack createItem() {
        return item.get();
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.apply(player);
    }

    public TranslationTextComponent getName() {
        return name;
    }

}

package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public enum ShinsuShape {

    NONE(() -> ItemStack.EMPTY, player -> -1.0),
    SHOVEL(() -> RegistryHandler.SHINSU_SHOVEL.get().getDefaultInstance(), player -> {
        Set<Block> blocks = new HashSet<>(Tags.Blocks.DIRT.getAllElements());
        blocks.addAll(BlockTags.SAND.getAllElements());
        blocks.addAll(Tags.Blocks.GRAVEL.getAllElements());
        StatisticsManager stats = player.getStats();
        int amount = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        return amount * 0.1;
    }),
    PICKAXE(() -> RegistryHandler.SHINSU_PICKAXE.get().getDefaultInstance(), player -> {
        Set<Block> blocks = new HashSet<>(Tags.Blocks.STONE.getAllElements());
        blocks.addAll(BlockTags.STONE_BRICKS.getAllElements());
        blocks.addAll(Tags.Blocks.SANDSTONE.getAllElements());
        blocks.addAll(Tags.Blocks.END_STONES.getAllElements());
        blocks.addAll(Tags.Blocks.NETHERRACK.getAllElements());
        blocks.addAll(Tags.Blocks.ORES.getAllElements());
        StatisticsManager stats = player.getStats();
        int amount = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        return amount * 0.1;
    }),
    AXE(() -> RegistryHandler.SHINSU_AXE.get().getDefaultInstance(), player -> {
        Set<Block> blocks = new HashSet<>(BlockTags.PLANKS.getAllElements());
        blocks.addAll(BlockTags.LOGS.getAllElements());
        StatisticsManager stats = player.getStats();
        int amount = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        return amount * 1.0;
    }),
    SWORD(() -> RegistryHandler.SHINSU_SWORD.get().getDefaultInstance(), player -> 0.25 * player.getStats().getValue(Stats.CUSTOM, Stats.DAMAGE_DEALT) * 0.5),
    HOE(() -> RegistryHandler.SHINSU_HOE.get().getDefaultInstance(), player -> {
        Set<Block> blocks = new HashSet<>(BlockTags.FLOWERS.getAllElements());
        blocks.addAll(BlockTags.CROPS.getAllElements());
        StatisticsManager stats = player.getStats();
        int amount = blocks.stream().mapToInt(block -> stats.getValue(Stats.BLOCK_MINED, block)).sum();
        return amount * 4.0;
    }),
    BOW(() -> RegistryHandler.SHINSU_BOW.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.ITEM_USED, Items.BOW) * 2.0);

    private final NonNullSupplier<ItemStack> item;
    private final Function<ServerPlayerEntity, Double> suitability;

    ShinsuShape(NonNullSupplier<ItemStack> item, Function<ServerPlayerEntity, Double> suitability) {
        this.item = item;
        this.suitability = suitability;
    }

    public static ShinsuShape get(String name) {
        for (ShinsuShape shape : values()) {
            if (shape.name().equals(name)) {
                return shape;
            }
        }
        return NONE;
    }

    public ItemStack createItem() {
        return item.get();
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.apply(player);
    }

}
